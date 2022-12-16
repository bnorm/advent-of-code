use std::collections::HashSet;
use std::ops::RangeInclusive;

use recap::Recap;
use serde::Deserialize;

pub fn part1() -> String {
    let y = 2000000;
    let readings = include_str!("res/input15.txt")
        .lines()
        .map(|line| SensorReading::new(line.parse::<SensorReadingInput>().unwrap()))
        .collect::<Vec<_>>();

    // Calculate sensor ranges and count of spaces
    let ranges = ranges_union(&readings.iter().filter_map(|r| r.line_range(y)).collect::<Vec<_>>());
    let mut count = ranges.iter().fold(0 as isize, |acc, r| acc + (r.end() - r.start() + 1));

    // Remove beacons which are on that row
    count -= readings.iter()
        .map(|r| (r.beacon_x, r.beacon_y))
        .collect::<HashSet<_>>().iter()
        .filter(|(_, beacon_y)| *beacon_y == y)
        .count() as isize;

    return format!("{:?}", count);
}

pub fn part2() -> String {
    let readings = include_str!("res/input15.txt")
        .lines()
        .map(|line| SensorReading::new(line.parse::<SensorReadingInput>().unwrap()))
        .collect::<Vec<_>>();

    let (x, y) = find_beacon(&readings, 4_000_000);
    return format!("{:?}", x * 4_000_000 + y);
}

#[derive(Debug, Copy, Clone, Deserialize, Recap)]
#[recap(regex = r#"Sensor at x=(?P<sensor_x>[-\d]+), y=(?P<sensor_y>[-\d]+): closest beacon is at x=(?P<beacon_x>[-\d]+), y=(?P<beacon_y>[-\d]+)"#)]
struct SensorReadingInput {
    pub sensor_x: isize,
    pub sensor_y: isize,
    pub beacon_x: isize,
    pub beacon_y: isize,
}

struct SensorReading {
    pub sensor_x: isize,
    pub sensor_y: isize,
    pub beacon_x: isize,
    pub beacon_y: isize,
    pub beacon_dist: isize,
}

impl SensorReading {
    fn new(input: SensorReadingInput) -> Self {
        return SensorReading {
            sensor_x: input.sensor_x,
            sensor_y: input.sensor_y,
            beacon_x: input.beacon_x,
            beacon_y: input.beacon_y,
            beacon_dist: (input.sensor_x - input.beacon_x).abs() + (input.sensor_y - input.beacon_y).abs(),
        };
    }

    fn line_range(&self, y: isize) -> Option<RangeInclusive<isize>> {
        let width = self.beacon_dist - (self.sensor_y - y).abs();
        return if width <= 0 {
            None
        } else {
            Some(self.sensor_x - width..=self.sensor_x + width)
        }
    }
}

fn find_beacon(readings: &Vec<SensorReading>, max: isize) -> (isize, isize) {
    for y in 0..=max {
        let mut ranges = vec![0..=max];
        for reading in readings {
            let cut = match reading.line_range(y) {
                Some(it) => it,
                None => continue,
            };

            ranges = range_dissection(&ranges, &cut);
            if ranges.len() == 0 {
                break
            }
        }

        if ranges.len() == 1 {
            let range = &ranges[0];
            if range.start() == range.end() {
                return (*range.start(), y)
            }
        }
    }
    panic!();
}

fn range_dissection(ranges: &Vec<RangeInclusive<isize>>, cut: &RangeInclusive<isize>) -> Vec<RangeInclusive<isize>> {
    let mut new = Vec::<RangeInclusive<isize>>::new();
    for range in ranges {
        if range.contains(cut.start()) {
            new.push(*range.start()..=*cut.start() - 1);
        }
        if range.contains(cut.end()) {
            new.push(*cut.end() + 1..=*range.end());
        }
        if range.start() > cut.end() || range.end() < cut.start() {
            new.push(range.clone());
        }
    }
    return new;
}

fn range_union(range: &RangeInclusive<isize>, cut: &RangeInclusive<isize>) -> Option<RangeInclusive<isize>> {
    if range.contains(cut.start()) {
        if range.contains(cut.end()) {
            return Some(range.clone())
        } else {
            return Some(*range.start()..=*cut.end())
        }
    }
    if cut.contains(range.start()) {
        if cut.contains(range.end()) {
            return Some(cut.clone())
        } else {
            return Some(*cut.start()..=*range.end())
        }
    }
    if range.end() + 1 == *cut.start() {
        return Some(*range.start()..=*cut.end())
    }
    if cut.end() + 1 == *range.start() {
        return Some(*cut.start()..=*range.end())
    }
    return None
}

fn ranges_union(range: &Vec<RangeInclusive<isize>>) -> Vec<RangeInclusive<isize>> {
    let mut sorted = Vec::from_iter(range.iter());
    sorted.sort_by(|a, b| a.start().cmp(b.start()));

    let mut union = Vec::<RangeInclusive<isize>>::new();
    let mut iter = sorted.iter();
    let mut current = match iter.next() {
        None => return union,
        Some(&it) => it.clone(),
    };

    while let Some(&it) = iter.next() {
        match range_union(&current, it) {
            None => {
                union.push(current.clone());
                current = it.clone();
            },
            Some(it) => {
                current = it
            }
        }
    }

    union.push(current);
    return union;
}
