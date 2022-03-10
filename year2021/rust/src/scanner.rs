use itertools::Itertools;
use recap::Recap;
use serde::Deserialize;
use std::collections::{HashMap, HashSet, VecDeque};
use std::hash::{Hash, Hasher};
use std::ops::{Add, Sub};

#[derive(Debug, PartialEq, Eq, Clone)]
pub struct Scanner {
    pub id: usize,
    pub location: Point,
    pub beacons: Vec<Point>,
}

impl Hash for Scanner {
    fn hash<H: Hasher>(&self, state: &mut H) {
        self.id.hash(state);
        // beacons are purposely excluded from the hash
    }
}

impl Scanner {
    fn new(id: usize, location: Point, beacons: Vec<Point>) -> Scanner {
        return Scanner {
            id,
            location,
            beacons,
        };
    }

    fn located(&self, location: Point) -> Scanner {
        return Scanner::new(self.id, location, self.beacons.clone());
    }

    pub fn locate(&self, scanners: &[Scanner]) -> Vec<Scanner> {
        let mut rotations = HashMap::<usize, Vec<Scanner>>::new();
        for s in scanners {
            rotations.insert(s.id, s.rotations());
        }

        let mut remaining = VecDeque::<&Scanner>::new();
        remaining.extend(scanners);

        let mut result = Vec::<Scanner>::new();
        println!("found={:?}", self);
        result.push(self.clone());

        let mut edges = Vec::<Scanner>::new();
        edges.push(self.clone());

        while !remaining.is_empty() {
            let mut newly_located = Vec::<Scanner>::new();
            let pending = remaining.drain(..).collect_vec();

            for next in &pending {
                let mut found = false;
                for existing in &edges {
                    let rotations = rotations.get(&next.id).unwrap();
                    if let Some(relative) = existing.locate_relative_with_rotations(rotations) {
                        println!("found={:?}", relative);
                        newly_located.push(relative.clone());
                        result.push(relative);
                        found = true;
                        break;
                    }
                }
                if !found {
                    remaining.push_back(next)
                }
            }

            edges = newly_located;
            if remaining.len() == pending.len() {
                break;
            } // infinite loop
        }

        return result;
    }

    fn rotations(&self) -> Vec<Scanner> {
        let mut beacon_rotations = self.beacons.iter().map(|p| p.rotations()).collect_vec();

        let mut result = Vec::<Scanner>::new();
        loop {
            let mut beacons = VecDeque::<Point>::new();
            for b in 0..self.beacons.len() {
                if let Some(p) = beacon_rotations[b].pop() {
                    beacons.push_front(p);
                }
            }
            if !beacons.is_empty() {
                result.push(Scanner::new(self.id, self.location, Vec::from(beacons)))
            } else {
                break;
            }
        }

        return result;
    }

    fn locate_relative_with_rotations(&self, rotations: &[Scanner]) -> Option<Scanner> {
        for other_rot in rotations {
            for my_beacon in &self.beacons {
                for other_beacon in &other_rot.beacons {
                    let delta = my_beacon - other_beacon;
                    if self.overlaps(&other_rot, &delta) {
                        return Some(other_rot.located(&self.location + &delta));
                    }
                }
            }
        }
        return None;
    }

    fn overlaps(&self, other: &Scanner, delta: &Point) -> bool {
        let other_beacons: HashSet<Point> = other.beacons.iter().map(|b| b + delta).collect();

        let mut common = 0;
        for b in &self.beacons {
            if other_beacons.contains(&b) {
                common += 1;
            }
        }

        return common >= 12;
    }
}

#[derive(Debug, PartialEq, Eq, Hash, Clone, Copy)]
pub struct Point {
    pub x: isize,
    pub y: isize,
    pub z: isize,
}

impl<'a, 'b> Add<&'b Point> for &'a Point {
    type Output = Point;

    fn add(self, rhs: &'b Point) -> Point {
        return Point::new(self.x + rhs.x, self.y + rhs.y, self.z + rhs.z);
    }
}

impl<'a, 'b> Sub<&'b Point> for &'a Point {
    type Output = Point;

    fn sub(self, rhs: &'b Point) -> Point {
        return Point::new(self.x - rhs.x, self.y - rhs.y, self.z - rhs.z);
    }
}

impl Point {
    fn new(x: isize, y: isize, z: isize) -> Point {
        return Point { x, y, z };
    }

    fn rotations(&self) -> Vec<Point> {
        fn rotate_x(result: &mut Vec<Point>, x: isize, y: isize, z: isize) {
            // To rotate 90d, swap non-axis values and make first negative
            result.push(Point::new(x, y, z));
            result.push(Point::new(x, -z, y));
            result.push(Point::new(x, -y, -z));
            result.push(Point::new(x, z, -y));
        }

        let mut result = Vec::<Point>::new();

        // Rotate to 6 different axis then rotate around x-axis 4 times
        rotate_x(&mut result, self.x, self.y, self.z); // Forward x
        rotate_x(&mut result, -self.y, self.x, self.z); // Forward y - 90d rotation around z
        rotate_x(&mut result, -self.x, -self.y, self.z); // Backward x - another 90d rotation around z
        rotate_x(&mut result, self.y, -self.x, self.z); // Backward y - another 90d rotation around z

        rotate_x(&mut result, -self.z, self.y, self.x); // Forward z - 90d rotation around y
        rotate_x(&mut result, self.z, self.y, -self.x); // Backward z - 180d rotation around y

        return result;
    }

    pub fn manhattan_distance(&self, other: &Point) -> isize {
        return (self.x - other.x).abs() + (self.y - other.y).abs() + (self.z - other.z).abs();
    }
}

//
// Unit tests
//

#[cfg(test)]
mod tests {
    use super::*;
    use itertools::Itertools;

    #[test]
    fn test_parse() {}

    #[test]
    fn test_rotations() {
        let start = Point::new(1, 2, 3);
        let rotations = start.rotations();
        assert_eq!(24, rotations.len());
        assert_eq!(24, rotations.into_iter().unique().count());
    }
}

//
// Parsing of report
//

#[derive(Debug, PartialEq, Eq, Hash, Deserialize, Recap)]
#[recap(regex = r#"--- scanner (?P<id>\d+) ---"#)]
struct ScannerInput {
    id: usize,
}

#[derive(Debug, PartialEq, Eq, Hash, Deserialize, Recap)]
#[recap(regex = r#"(?P<x>-?\d+),(?P<y>-?\d+),(?P<z>-?\d+)"#)]
struct BeaconInput {
    x: isize,
    y: isize,
    z: isize,
}

pub fn parse_scanner_report(contents: &str) -> Vec<Scanner> {
    let mut scanners = Vec::<Scanner>::new();
    let mut scanner: isize = -1;
    let mut beacons = Vec::<Point>::new();

    for line in contents.lines() {
        if let Ok(s) = line.parse::<ScannerInput>() {
            if scanner >= 0 && !beacons.is_empty() {
                scanners.push(Scanner::new(scanner as usize, Point::new(0, 0, 0), beacons));
                beacons = Vec::<Point>::new();
            }
            scanner = s.id as isize;
        } else if let Ok(b) = line.parse::<BeaconInput>() {
            beacons.push(Point::new(b.x, b.y, b.z))
        }
    }

    if !beacons.is_empty() {
        scanners.push(Scanner::new(scanner as usize, Point::new(0, 0, 0), beacons));
    }

    return scanners;
}
