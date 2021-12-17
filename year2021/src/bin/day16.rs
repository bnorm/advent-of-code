use std::fs;
use std::time::Instant;

use year2021::packet::Packet;

fn main() {
    let start = Instant::now();
    println!("[part1] answer={:?}", part1());
    println!("[part2] answer={:?}", part2());
    println!("finished in {:?}", start.elapsed());
}

fn part1() -> Option<u64> {
    let instant = Instant::now();
    let packet = read_input();

    let result = packet.sum_version();

    println!("[part1] time={:?}", instant.elapsed());
    return Some(result);
}

fn part2() -> Option<u128> {
    let instant = Instant::now();
    let packet = read_input();

    let result = packet.calculate();

    println!("[part2] time={:?}", instant.elapsed());
    return Some(result);
}

fn read_input() -> Packet {
    let filename = "res/input16.txt";
    let contents = fs::read_to_string(filename).unwrap();
    return Packet::parse(&contents);
}
