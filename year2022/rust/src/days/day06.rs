use std::collections::{HashSet, VecDeque};

pub fn part1() -> String {
    let input = include_str!("res/input06.txt").lines().collect::<Vec<_>>();

    let mut window = VecDeque::<char>::new();

    let mut index = 0;
    for (i, c) in input[0].chars().enumerate() {
        window.push_back(c);
        if window.len() == 4 {
            if HashSet::<&char>::from_iter(window.iter()).len() == 4 {
                index = i + 1;
                break
            }
            window.pop_front();
        }
    }

    return format!("{:?}", index);
}

pub fn part2() -> String {
    let input = include_str!("res/input06.txt").lines().collect::<Vec<_>>();

    let mut window = VecDeque::<char>::new();

    let mut index = 0;
    for (i, c) in input[0].chars().enumerate() {
        window.push_back(c);
        if window.len() == 14 {
            if HashSet::<&char>::from_iter(window.iter()).len() == 14 {
                index = i + 1;
                break
            }
            window.pop_front();
        }
    }

    return format!("{:?}", index);
}
