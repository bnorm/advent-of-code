use std::ops::RangeInclusive;

use recap::Recap;
use serde::Deserialize;

pub fn part1() -> String {
    let readings = include_str!("res/input15.txt")
        .lines()
        .map(|line| SensorReading::new(line.parse::<SensorReadingInput>().unwrap()))
        .collect::<Vec<_>>();

    let y = 2000000;
    let min_x = readings.iter().fold(0, |acc, r| acc.min(r.sensor_x - r.line_width(y)));
    let max_x = readings.iter().fold(0, |acc, r| acc.max(r.sensor_x + r.line_width(y)));

    let mut count = 0;
    for x in min_x..=max_x {
        let is_beacon = readings.iter().fold(false, |acc, r| acc || is_beacon(x, y, r));
        let possible_beacon = readings.iter().fold(true, |acc, r| acc && possible_beacon(x, y, r));
        if !is_beacon && !possible_beacon {
            count += 1;
        }
    }

    return format!("{:?}", count);
}

pub fn part2() -> String {
    let readings = include_str!("res/input15.txt")
        .lines()
        .map(|line| SensorReading::new(line.parse::<SensorReadingInput>().unwrap()))
        .collect::<Vec<_>>();

    let (x, y) = find_beacon(&readings);
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

    fn line_width(&self, y: isize) -> isize {
        let line_dist = (self.sensor_y - y).abs();
        return (self.beacon_dist - line_dist).max(0);
    }

    fn line_range(&self, y: isize) -> Option<RangeInclusive<isize>> {
        let line_dist = (self.sensor_y - y).abs();
        let width = self.beacon_dist - line_dist;
        return if width <= 0 {
            None
        } else {
            Some(self.sensor_x - width..=self.sensor_x + width)
        }
    }
}

fn find_beacon(readings: &Vec<SensorReading>) -> (isize, isize) {
    for y in 0..=4_000_000 {
        let mut ranges = vec![0..=4_000_000];
        for reading in readings {
            let cut = match reading.line_range(y) {
                Some(it) => it,
                None => continue,
            };

            ranges = dissect_range(&ranges, &cut);
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

fn dissect_range(original: &Vec<RangeInclusive<isize>>, cut: &RangeInclusive<isize>) -> Vec<RangeInclusive<isize>> {
    let mut new = Vec::<RangeInclusive<isize>>::new();

    for range in original {
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

fn possible_beacon(x: isize, y: isize, reading: &SensorReading) -> bool {
    if x == reading.beacon_x && y == reading.beacon_y { return false; }

    let point_dist = (reading.sensor_x - x).abs() + (reading.sensor_y - y).abs();
    return point_dist > reading.beacon_dist;
}

fn is_beacon(x: isize, y: isize, reading: &SensorReading) -> bool {
    return x == reading.beacon_x && y == reading.beacon_y;
}
