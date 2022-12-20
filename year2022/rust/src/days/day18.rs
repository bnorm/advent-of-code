use std::collections::{BinaryHeap, HashSet};

use recap::Recap;
use serde::Deserialize;

pub fn part1() -> String {
    let scan = Scan::new(include_str!("res/input18.txt"));
    return format!("{:?}", scan.surface_area());
}

pub fn part2() -> String {
    let scan = Scan::new(include_str!("res/input18.txt"));
    return format!("{:?}", scan.surface_area_external());
}

#[derive(Debug, Copy, Clone, Ord, PartialOrd, Hash, Eq, PartialEq, Deserialize, Recap)]
#[recap(regex = r#"(?P<x>\d+),(?P<y>\d+),(?P<z>\d+)"#)]
struct Cube {
    pub x: isize,
    pub y: isize,
    pub z: isize,
}

impl Cube {
    fn new(x: isize, y: isize, z: isize) -> Self {
        return Self { x, y, z };
    }

    fn neighbors(&self) -> impl Iterator<Item=Cube> + '_ {
        let mut num = 0;
        return std::iter::from_fn(move || {
            loop {
                num += 1;
                return match num {
                    1 => Some(Cube::new(self.x - 1, self.y, self.z)), // Left
                    2 => Some(Cube::new(self.x + 1, self.y, self.z)), // Right
                    3 => Some(Cube::new(self.x, self.y - 1, self.z)), // Forward
                    4 => Some(Cube::new(self.x, self.y + 1, self.z)), // Backward
                    5 => Some(Cube::new(self.x, self.y, self.z - 1)), // Up
                    6 => Some(Cube::new(self.x, self.y, self.z + 1)), // Down
                    _ => None // Exit
                }
            }
        });
    }
}

struct Scan {
    pub cubes: HashSet<Cube>,
}

impl Scan {
    fn new(input: &str) -> Self {
        let cubes = input.lines()
            .map(|line| line.parse::<Cube>().unwrap())
            .collect::<HashSet<_>>();

        return Self { cubes };
    }

    fn surface_area(&self) -> usize {
        let mut area = 0;
        for cube in &self.cubes {
            for adjacent in cube.neighbors() {
                if !self.cubes.contains(&adjacent) {
                    area += 1;
                }
            }
        }
        return area;
    }

    fn surface_area_external(&self) -> usize {
        let mut max_x = isize::MIN;
        let mut min_x = isize::MAX;
        let mut max_y = isize::MIN;
        let mut min_y = isize::MAX;
        let mut max_z = isize::MIN;
        let mut min_z = isize::MAX;
        for cube in &self.cubes {
            max_x = max_x.max(cube.x + 1);
            min_x = min_x.min(cube.x - 1);
            max_y = max_y.max(cube.y + 1);
            min_y = min_y.min(cube.y - 1);
            max_z = max_z.max(cube.z + 1);
            min_z = min_z.min(cube.z - 1);
        }

        let mut external_cubes = HashSet::<Cube>::new();
        let mut search = BinaryHeap::<Cube>::new();
        search.push(Cube::new(min_x, min_y, min_z));
        while let Some(node) = search.pop() {
            let inbounds = node.x >= min_x && node.x <= max_x &&
                node.y >= min_y && node.y <= max_y &&
                node.z >= min_z && node.z <= max_z;
            if inbounds && !self.cubes.contains(&node) && external_cubes.insert(node) {
                for neighbor in node.neighbors() {
                    search.push(neighbor);
                }
            }
        }

        let mut area = 0;
        for cube in &self.cubes {
            for adjacent in cube.neighbors() {
                if external_cubes.contains(&adjacent) {
                    area += 1;
                }
            }
        }
        return area;
    }
}
