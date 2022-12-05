use std::collections::VecDeque;

use recap::Recap;
use serde::Deserialize;

pub fn part1() -> String {
    let input = include_str!("res/input05.txt").lines().collect::<Vec<_>>();
    let sections = input.split(|line| { line.len() == 0 }).collect::<Vec<_>>();

    let mut loading_dock = LoadingDock::new(sections[0]);
    let instructions = sections[1].iter().map(|line| { line.parse::<Instruction>().unwrap() }).collect::<Vec<_>>();

    for instruction in instructions {
        loading_dock.perform_9000(&instruction);
    }

    return format!("{:?}", loading_dock.tops());
}

pub fn part2() -> String {
    let input = include_str!("res/input05.txt").lines().collect::<Vec<_>>();
    let sections = input.split(|line| { line.len() == 0 }).collect::<Vec<_>>();

    let mut loading_dock = LoadingDock::new(sections[0]);
    let instructions = sections[1].iter().map(|line| { line.parse::<Instruction>().unwrap() }).collect::<Vec<_>>();

    for instruction in instructions {
        loading_dock.perform_9001(&instruction);
    }

    return format!("{:?}", loading_dock.tops());
}

#[derive(Debug, Copy, Clone, Hash, Eq, PartialEq, Deserialize, Recap)]
#[recap(regex = r#"move (?P<count>\d+) from (?P<source>\d+) to (?P<destination>\d+)"#)]
pub struct Instruction {
    pub count: usize,
    pub source: usize,
    pub destination: usize,
}

#[derive(Debug, Hash, Eq, PartialEq)]
pub struct LoadingDock {
    pub stacks: Vec<VecDeque<char>>,
}

impl LoadingDock {
    pub fn new(input: &[&str]) -> Self {
        let mut stacks = Vec::new();

        for line in input {
            let chars = line.chars().collect::<Vec<_>>();
            for stack_index in 0..(line.len() + 1) / 4 {
                while stacks.len() <= stack_index {
                    stacks.push(VecDeque::new())
                }

                let prefix = chars[stack_index * 4];
                let item = chars[stack_index * 4 + 1];
                if prefix == '[' && item != ' ' {
                    let stack = stacks.get_mut(stack_index).unwrap();
                    stack.push_back(item);
                }
            }
        }

        return Self { stacks };
    }

    fn perform_9000(&mut self, instruction: &Instruction) {
        for _ in 0..instruction.count {
            let source = self.stacks.get_mut(instruction.source - 1).unwrap();
            let item = source.pop_front().unwrap();

            let destination = self.stacks.get_mut(instruction.destination - 1).unwrap();
            destination.push_front(item);
        }
    }

    fn perform_9001(&mut self, instruction: &Instruction) {
        let mut items = Vec::new();
        for _ in 0..instruction.count {
            let source = self.stacks.get_mut(instruction.source - 1).unwrap();
            items.push(source.pop_front().unwrap());
        }

        items.reverse();
        for item in items {
            let destination = self.stacks.get_mut(instruction.destination - 1).unwrap();
            destination.push_front(item);
        }
    }

    fn tops(&self) -> String {
        return String::from_iter(
            self.stacks.iter()
                .map(|stack| { *stack.get(0).unwrap() })
                .collect::<Vec<_>>()
        );
    }
}


