use std::fs;

use itertools::Itertools;

fn main() {
    part1();
    part2();
}

fn part1() {
    let filename = "input.txt";
    let contents = fs::read_to_string(filename)
        .expect("Something went wrong reading the file");

    let mut count = 0;
    for (prev, next) in contents.lines().tuple_windows() {
        if (prev.parse::<i32>().unwrap() < next.parse::<i32>().unwrap()) {
            count = count + 1;
        }
    }

    println!("[part1] Count: {}", count);
}


fn part2() {
    let filename = "input.txt";

    let contents = fs::read_to_string(filename)
        .expect("Something went wrong reading the file");

    let mut count = 0;
    for (n1, n2, n3, n4) in contents.lines().tuple_windows::<(_, _, _, _)>() {
        let prev = n1.parse::<i32>().unwrap() + n2.parse::<i32>().unwrap() + n3.parse::<i32>().unwrap();
        let next = n2.parse::<i32>().unwrap() + n3.parse::<i32>().unwrap() + n4.parse::<i32>().unwrap();
        // println!("[part2] prev={} next={}", prev, next);
        if prev < next {
            count = count + 1;
        }
    }

    println!("[part2] Count: {}", count);
}
