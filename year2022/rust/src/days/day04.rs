use recap::Recap;
use serde::Deserialize;

pub fn part1() -> String {
    let input = include_str!("res/input04.txt").lines().collect::<Vec<_>>();
    let pairs = input.iter().map(|line| { line.parse::<ElfPair>().unwrap() }).collect::<Vec<_>>();
    let count = pairs.iter().filter(|pair| { pair.overlap_completely() }).count();
    return format!("{:?}", count);
}

pub fn part2() -> String {
    let input = include_str!("res/input04.txt").lines().collect::<Vec<_>>();
    let pairs = input.iter().map(|line| { line.parse::<ElfPair>().unwrap() }).collect::<Vec<_>>();
    let count = pairs.iter().filter(|pair| { pair.overlap_partially() }).count();
    return format!("{:?}", count);
}

#[derive(Debug, Copy, Clone, Hash, Eq, PartialEq, Deserialize, Recap)]
#[recap(regex = r#"(?P<first_start>\d+)-(?P<first_end>\d+),(?P<second_start>\d+)-(?P<second_end>\d+)"#)]
pub struct ElfPair {
    pub first_start: i32,
    pub first_end: i32,
    pub second_start: i32,
    pub second_end: i32,
}

impl ElfPair {
    fn overlap_completely(&self) -> bool {
        return (self.first_start <= self.second_start && self.first_end >= self.second_end) ||
            (self.second_start <= self.first_start && self.second_end >= self.first_end);
    }

    fn overlap_partially(&self) -> bool {
        return (self.first_start <= self.second_start && self.second_start <= self.first_end) ||
            (self.first_start <= self.second_end && self.second_end <= self.first_end) ||
            (self.second_start <= self.first_start && self.first_start <= self.second_end) ||
            (self.second_start <= self.first_end && self.first_end <= self.second_end);
    }
}
