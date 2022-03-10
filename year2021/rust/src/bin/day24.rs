use std::collections::VecDeque;
use std::fs;
use std::time::Instant;

use itertools::Itertools;

use year2021::alu::{Input, Instruction, Variable, ALU};

fn main() {
    let start = Instant::now();
    let input = read_input("res/input24.txt");
    println!("[part1] answer={:?}", part1(&input));
    println!("[part2] answer={:?}", part2(&input));
    println!("finished in {:?}", start.elapsed());
}

fn read_input(file: &str) -> Vec<Instruction> {
    let filename = file;
    let contents = fs::read_to_string(filename).unwrap();
    return contents
        .lines()
        .map(|line| Instruction::parse(line))
        .collect_vec();
}

fn part1(input: &Vec<Instruction>) -> Option<usize> {
    let instant = Instant::now();

    let sections = input
        .split(|instruction| match instruction {
            Instruction::Inp(_) => true,
            _ => false,
        })
        .filter(|s| !s.is_empty())
        .collect_vec();

    let mut stack = VecDeque::<String>::new();
    let mut digit = 0;
    for section in sections {
        digit += 1;

        let push = match &section[3] {
            Instruction::Div(_, i) => match i {
                Input::Value(v) => *v == 1,
                _ => unreachable!(),
            },
            _ => unreachable!(),
        };
        let c = match &section[4] {
            Instruction::Add(_, i) => match i {
                Input::Value(v) => *v,
                _ => unreachable!(),
            },
            _ => unreachable!(),
        };
        let b = match &section[14] {
            Instruction::Add(_, i) => match i {
                Input::Value(v) => *v,
                _ => unreachable!(),
            },
            _ => unreachable!(),
        };

        if push {
            stack.push_front(format!("d[{}] + {}", digit, b));
        } else {
            println!("{} + {} == d[{}]", stack.pop_front().unwrap(), c, digit);
        }
    }

    // d[4] + 11 + -3 == d[5]
    // d[7] + 14 + -16 == d[8]
    // d[9] + 15 + -8 == d[10]
    // d[6] + 12 + -12 == d[11]
    // d[3] + 2 + -7 == d[12]
    // d[2] + 8 + -6 == d[13]
    // d[1] + 7 + -11 == d[14]

    // 1 2 3 4 5 6 7 8 9 10 11 12 13 14
    // 9 7 9 1 9 9 9 7 2  9  9  4  9  5
    // 97919997299495

    println!("[part1] time={:?}", instant.elapsed());
    return None;
}

fn part2(_input: &Vec<Instruction>) -> Option<usize> {
    let instant = Instant::now();

    // d[4] + 8 == d[5]
    // d[7] - 2 == d[8]
    // d[9] + 7 == d[10]
    // d[6] + 0 == d[11]
    // d[3] - 5 == d[12]
    // d[2] + 2 == d[13]
    // d[1] - 4 == d[14]

    // 1 2 3 4 5 6 7 8 9 10 11 12 13 14
    // 5 1 6 1 9 1 3 1 1  8  1  1  3  1
    // 51619131181131

    println!("[part2] time={:?}", instant.elapsed());
    return None;
}
