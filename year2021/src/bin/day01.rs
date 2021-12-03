use std::fs;

use itertools::Itertools;

fn main() {
    part1();
    part2();
}

fn part1() {
    let filename = "res/input01.txt";
    let contents = fs::read_to_string(filename)
        .expect("Something went wrong reading the file");

    let count = contents.lines()
        .map(|x| { x.parse::<i32>().expect("Unable to parse file line as int") })
        .tuple_windows::<(_, _)>()
        .filter(|(prev, next)| { prev < next })
        .count();

    println!("[part1] Count: {}", count);
}

fn part2() {
    let filename = "res/input01.txt";
    let contents = fs::read_to_string(filename)
        .expect("Something went wrong reading the file");

    let count = contents.lines()
        .map(|x| { x.parse::<i32>().expect("Unable to parse file line as int") })
        .tuple_windows::<(_, _, _, _)>()
        .filter(|(n1, n2, n3, n4)| { (n1 + n2 + n3) < (n2 + n3 + n4) })
        .count();

    println!("[part2] Count: {}", count);
}
