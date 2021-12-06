use std::fs;

const BITS: u32 = 12;

fn main() {
    println!("[part1] answer={:?}", part1());
    println!("[part2] answer={:?}", part2());
}

fn part1() -> Option<u32> {
    let filename = "res/input03.txt";
    let contents = fs::read_to_string(filename)
        .expect("Something went wrong reading the file");

    let lines: Vec<u32> = contents.lines()
        .filter_map(|line| u32::from_str_radix(line, 2).ok())
        .collect();

    let g = gamma(&BITS, &lines);
    let e = epsilon(&BITS, &lines);

    println!("[part1] gamma={} epsilon={}", g, e);
    return Some(g * e);
}

fn part2() -> Option<u32> {
    let filename = "res/input03.txt";
    let contents = fs::read_to_string(filename)
        .expect("Something went wrong reading the file");

    let lines: Vec<u32> = contents.lines()
        .filter_map(|line| u32::from_str_radix(line, 2).ok())
        .collect();

    let mut ogr = 0;
    let mut remaining = lines.to_vec();
    for bit in (0..BITS).rev() {
        ogr = (ogr << 1) + most_common(&bit, &remaining);
        remaining.retain(|x| { ogr == (x >> bit) });

        if remaining.len() == 1 {
            ogr = remaining[0];
            break;
        }
    }

    let mut csr = 0;
    remaining = lines.to_vec();
    for bit in (0..BITS).rev() {
        csr = (csr << 1) + (1 - most_common(&bit, &remaining));
        remaining.retain(|x| { csr == (x >> bit) });

        if remaining.len() == 1 {
            csr = remaining[0];
            break;
        }
    }

    println!("[part2] oxygen_generator_rating={} co2_scrubber_rating={}", ogr, csr);
    return Some(ogr * csr);
}

fn gamma(bits: &u32, lines: &Vec<u32>) -> u32 {
    let mut gamma = 0;
    for bit in (0..*bits).rev() {
        gamma = (gamma << 1) + most_common(&bit, lines);
    }
    return gamma;
}

fn epsilon(bits: &u32, lines: &Vec<u32>) -> u32 {
    let mut epsilon = 0;
    for bit in (0..*bits).rev() {
        epsilon = (epsilon << 1) + (1 - most_common(&bit, lines));
    }
    return epsilon;
}

fn most_common(bit: &u32, lines: &Vec<u32>) -> u32 {
    let half_size = lines.len() as f32 / 2.0;
    let mut count = 0;
    for line in lines {
        count += (line >> bit) & 1
    }

    return if (count as f32) >= half_size { 1 } else { 0 };
}
