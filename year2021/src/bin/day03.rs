use std::fs;

use itertools::Itertools;

fn main() {
    part1();
    part2();
}

fn part1() {
    let filename = "res/input03.txt";
    let contents = fs::read_to_string(filename)
        .expect("Something went wrong reading the file");

    let bits = &12;
    let lines = contents.lines()
        .filter(|line| { line.len() > 0 })
        .map(|line| { i32::from_str_radix(line, 2).unwrap() as i32 })
        .collect_vec();

    let g = gamma(bits, &lines);
    let e = epsilon(bits, &lines);

    println!("[part1] gamma={} epsilon={} answer={}", g, e, g * e);
}

fn part2() {
    let filename = "res/input03.txt";
    let contents = fs::read_to_string(filename)
        .expect("Something went wrong reading the file");

    let bits = &12;
    let lines = contents.lines()
        .filter(|line| { line.len() > 0 })
        .map(|line| { i32::from_str_radix(line, 2).unwrap() as i32 })
        .collect_vec();

    let mut ogr = 0;
    let mut remaining = lines.to_vec();
    for bit in (0..*bits).rev() {
        ogr = (ogr << 1) + most_common(&bit, &remaining);
        remaining.retain(|x| { ogr == (x >> bit) });

        if remaining.len() == 1 {
            ogr = remaining[0];
            break;
        }
    }

    let mut csr = 0;
    remaining = lines.to_vec();
    for bit in (0..*bits).rev() {
        csr = (csr << 1) + (1 - most_common(&bit, &remaining));
        remaining.retain(|x| { csr == (x >> bit) });

        if remaining.len() == 1 {
            csr = remaining[0];
            break;
        }
    }

    println!("[part2] oxygen_generator_rating={} co2_scrubber_rating={} answer={}", ogr, csr, ogr * csr);
}

fn gamma(bits: &usize, lines: &Vec<i32>) -> i32 {
    let mut gamma = 0;
    for bit in (0..*bits).rev() {
        gamma = (gamma << 1) + most_common(&bit, lines);
    }
    return gamma;
}

fn epsilon(bits: &usize, lines: &Vec<i32>) -> i32 {
    let mut epsilon = 0;
    for bit in (0..*bits).rev() {
        epsilon = (epsilon << 1) + (1 - most_common(&bit, lines));
    }
    return epsilon;
}

fn most_common(bit: &usize, lines: &Vec<i32>) -> i32 {
    let half_size = lines.len() as f32 / 2.0;
    let mut count = 0;
    for line in lines {
        count += (line >> bit) & 1
    }

    return if (count as f32) >= half_size { 1 } else { 0 };
}
