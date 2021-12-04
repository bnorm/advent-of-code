use std::fs;
use std::ops::Index;

use itertools::{any, Itertools};

fn main() {
    part1();
    part2();
}

fn part1() {
    let filename = "res/input04.txt";
    let contents = fs::read_to_string(filename)
        .expect("Something went wrong reading the file");

    let size = 5;
    let lines = contents.lines()
        .collect_vec();

    let (numbers, mut boards) = parse(lines);

    for number in numbers {
        for board in &mut boards {
            let index: Option<usize> = board.iter().position(|&x| { x == number });
            match index {
                Some(i) => board[i] = -board[i],
                None => {}
            }

            let complete = check_board(size, &board);
            if complete {
                let score = board.iter().filter(|&x| { x >= &0 }).fold(0, |a, b| { a + b });
                println!("[part1] number={} score={} answer={}", number, score, number * score);
                return;
            }
        }
    }
}

fn part2() {
    let filename = "res/input04.txt";
    let contents = fs::read_to_string(filename)
        .expect("Something went wrong reading the file");

    let size = 5;
    let lines = contents.lines()
        .collect_vec();

    let (numbers, mut boards) = parse(lines);

    for number in numbers {
        for board in &mut boards {
            let index: Option<usize> = board.iter().position(|&x| { x == number });
            match index {
                Some(i) => board[i] = -board[i],
                None => {}
            }
        }

        let remaining = boards.len();
        boards.retain(|board| {
            let complete = check_board(size, &board);
            if complete && remaining == 1 {
                let score = board.iter().filter(|&x| { x >= &0 }).fold(0, |a, b| { a + b });
                println!("[part2] number={} score={} answer={}", number, score, number * score);
            }
            return !complete;
        });
    }
}

fn parse(lines: Vec<&str>) -> (Vec<i32>, Vec<Vec<i32>>) {
    let mut numbers: Vec<i32> = vec!();
    let mut boards: Vec<Vec<i32>> = vec!();
    for section in lines.split(|line| { line.len() == 0 }) {
        if section.len() == 1 {
            numbers.extend(section[0].split(',').map(|x| { x.parse::<i32>().unwrap() }));
        } else {
            let mut board: Vec<i32> = vec!();
            for line in section {
                board.extend(line.split_whitespace().map(|x| { x.parse::<i32>().unwrap() }));
            }
            boards.push(board);
        }
    }
    return (numbers, boards);
}

fn check_board(size: usize, board: &Vec<i32>) -> bool {
    // Check for complete rows
    for row in 0..size {
        let mut complete = true;
        for column in 0..size {
            complete = complete && board[column * size + row] < 1
        }
        if complete { return true; }
    }

    // Check for complete columns
    for column in 0..size {
        let mut complete = true;
        for row in 0..size {
            complete = complete && board[column * size + row] < 1
        }
        if complete { return true; }
    }

    return false;
}
