use std::cmp::max;
use std::fs;
use std::time::Instant;
use year2021::snailfish::Node;

fn main() {
    let start = Instant::now();
    println!("[part1] answer={:?}", part1());
    println!("[part2] answer={:?}", part2());
    println!("finished in {:?}", start.elapsed());
}

fn part1() -> Option<u128> {
    let instant = Instant::now();
    let nodes = read_input();
    let result = Node::add_nodes(&nodes).unwrap().magnitude();
    println!("[part1] time={:?}", instant.elapsed());
    return Some(result);
}

fn part2() -> Option<u128> {
    let instant = Instant::now();
    let nodes = read_input();

    let mut result = 0;
    for i in 0..nodes.len() {
        for j in i + 1..nodes.len() {
            result = max(result, (&nodes[i] + &nodes[j]).magnitude());
            result = max(result, (&nodes[j] + &nodes[i]).magnitude());
        }
    }

    println!("[part2] time={:?}", instant.elapsed());
    return Some(result);
}

fn read_input() -> Vec<Node> {
    let filename = "res/input18.txt";
    let contents = fs::read_to_string(filename).unwrap();
    return contents.lines()
        .filter_map(|l| l.parse::<Node>().ok())
        .collect();
}
