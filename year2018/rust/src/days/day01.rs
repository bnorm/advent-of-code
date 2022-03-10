use std::collections::HashSet;

use itertools::Itertools;

pub fn part1() -> String {
    let content = include_str!("res/input01.txt");
    let result = content
        .lines()
        .map(|v| {
            v.parse::<isize>()
                .expect(format!("invalid number={}", v).as_ref())
        })
        .fold(0, |acc, v| acc + v);

    return format!("{}", result);
}

pub fn part2() -> String {
    let content = include_str!("res/input01.txt");
    let numbers = content
        .lines()
        .map(|v| {
            v.parse::<isize>()
                .expect(format!("invalid number={}", v).as_ref())
        })
        .collect_vec();

    let mut current = 0;
    let mut previous = HashSet::<isize>::new();
    previous.insert(current);
    for i in 0.. {
        current += numbers[i % numbers.len()];
        if !previous.insert(current) {
            break;
        }
    }

    return format!("{}", current);
}
