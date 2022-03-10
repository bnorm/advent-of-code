use std::collections::VecDeque;
use std::fs;
use std::time::Instant;

use itertools::Itertools;

fn main() {
    let start = Instant::now();
    println!("[part1] answer={:?}", part1());
    println!("[part2] answer={:?}", part2());
    println!("finished in {:?}", start.elapsed());
}

fn part1() -> Option<u64> {
    let start = Instant::now();
    let input = read_input()?;

    let mut result = 0;
    for line in input {
        result += score_corruption(&line).unwrap_or(0);
    }

    println!("[part1] time={:?}", start.elapsed());
    return Some(result);
}

fn part2() -> Option<u64> {
    let start = Instant::now();
    let input = read_input()?;

    let mut results = input
        .iter()
        .filter_map(|line| score_incomplete(&line))
        .collect_vec();

    results.sort();
    let result = results[results.len() / 2];

    println!("[part2] time={:?}", start.elapsed());
    return Some(result);
}

fn read_input() -> Option<Vec<Vec<char>>> {
    let filename = "res/input10.txt";
    let contents = fs::read_to_string(filename).ok()?;

    let input = contents
        .lines()
        .map(|line| line.chars().collect_vec())
        .collect_vec();

    return Some(input);
}

fn score_corruption(line: &Vec<char>) -> Option<u64> {
    let mut stack: VecDeque<&char> = VecDeque::new();

    for c in line {
        // println!("c={:?} => stack={:?}", c, stack);
        let result = match c {
            '(' | '{' | '[' | '<' => {
                stack.push_front(c);
                None
            }
            ')' => validate(stack.pop_front().unwrap(), &'('),
            ']' => validate(stack.pop_front().unwrap(), &'['),
            '}' => validate(stack.pop_front().unwrap(), &'{'),
            '>' => validate(stack.pop_front().unwrap(), &'<'),
            _ => None,
        };
        if result != None {
            return result;
        }
    }

    return None;
}

fn score_incomplete(line: &Vec<char>) -> Option<u64> {
    let mut stack: VecDeque<&char> = VecDeque::new();

    for c in line {
        let result = match c {
            '(' | '{' | '[' | '<' => {
                stack.push_front(c);
                None
            }
            ')' => validate(stack.pop_front().unwrap(), &'('),
            ']' => validate(stack.pop_front().unwrap(), &'['),
            '}' => validate(stack.pop_front().unwrap(), &'{'),
            '>' => validate(stack.pop_front().unwrap(), &'<'),
            _ => None,
        };
        if result != None {
            return None;
        }
    }

    let mut result = 0;
    for c in stack {
        result = 5 * result + score_close(c);
    }
    return Some(result);
}

fn validate(expected: &char, actual: &char) -> Option<u64> {
    return if expected != actual {
        // println!("expected={:?} actual={:?}", expected, actual);
        Some(score_mismatch(actual))
    } else {
        None
    };
}

fn score_mismatch(actual: &char) -> u64 {
    return match actual {
        '(' => 3,
        '[' => 57,
        '{' => 1197,
        '<' => 25137,
        _ => todo!(),
    };
}

fn score_close(actual: &char) -> u64 {
    return match actual {
        '(' => 1,
        '[' => 2,
        '{' => 3,
        '<' => 4,
        _ => todo!(),
    };
}
