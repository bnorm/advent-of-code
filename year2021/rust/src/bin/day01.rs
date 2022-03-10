use std::fs;

use itertools::Itertools;

fn main() {
    println!("[part1] answer={:?}", part1());
    println!("[part2] answer={:?}", part2());
}

fn part1() -> Option<usize> {
    let filename = "res/input01.txt";
    let contents = fs::read_to_string(filename).ok()?;

    let count = contents
        .lines()
        .filter_map(|x| x.parse::<i32>().ok())
        .tuple_windows::<(_, _)>()
        .filter(|(prev, next)| prev < next)
        .count();

    return Some(count);
}

fn part2() -> Option<usize> {
    let filename = "res/input01.txt";
    let contents = fs::read_to_string(filename).ok()?;

    let count = contents
        .lines()
        .filter_map(|x| x.parse::<i32>().ok())
        .tuple_windows::<(_, _, _, _)>()
        .filter(|(n1, n2, n3, n4)| (n1 + n2 + n3) < (n2 + n3 + n4))
        .count();

    return Some(count);
}
