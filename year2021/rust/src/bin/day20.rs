use std::fs;
use std::time::Instant;

use itertools::Itertools;

use year2021::grid::{Grid, Position};

type Image = Grid<bool>;

struct Input {
    pub image: Image,
    pub enhancement: Vec<bool>,
}

fn main() {
    let start = Instant::now();
    let input = read_input();
    println!("[part1] answer={:?}", part1(&input));
    println!("[part2] answer={:?}", part2(&input));
    println!("finished in {:?}", start.elapsed());
}

fn read_input() -> Input {
    let filename = "res/input20.txt";
    let contents = fs::read_to_string(filename).unwrap();

    let mut lines = contents.lines();
    let enhancement = lines
        .next()
        .unwrap()
        .chars()
        .map(|c| c == '#')
        .collect_vec();
    lines.next().unwrap();

    let values = lines
        .map(|line| line.chars().map(|c| c == '#').collect_vec())
        .collect_vec();
    let num_rows = values.len();
    let num_cols = values[0].len();
    let image = Grid::new(values, num_rows, num_cols);

    return Input { image, enhancement };
}

fn part1(input: &Input) -> Option<usize> {
    let instant = Instant::now();

    let mut image = input.image.clone();
    image = enhance(&image, &input.enhancement, 0);
    image = enhance(&image, &input.enhancement, 1);

    // display(&image);
    let lit = image.iter().filter(|v| **v).count();
    println!("[part1] time={:?}", instant.elapsed());
    return Some(lit);
}

fn part2(input: &Input) -> Option<usize> {
    let instant = Instant::now();

    let mut image = input.image.clone();
    for _ in 0..25 {
        image = enhance(&image, &input.enhancement, 0);
        image = enhance(&image, &input.enhancement, 1);
    }

    // display(&image);
    let lit = image.iter().filter(|v| **v).count();
    println!("[part2] time={:?}", instant.elapsed());
    return Some(lit);
}

fn enhance(image: &Image, enhancement: &Vec<bool>, default: usize) -> Image {
    let expand = 1;
    let mut new_image = Grid::new(
        vec![vec![false; image.num_cols + 2 * expand]; image.num_rows + 2 * expand],
        image.num_rows + 2 * expand,
        image.num_cols + 2 * expand,
    );
    for r in 0..image.num_rows + 2 * expand {
        for c in 0..image.num_cols + 2 * expand {
            let value = read(
                image,
                r as isize - expand as isize,
                c as isize - expand as isize,
                default,
            );
            let new_value = enhancement[value as usize];
            // println!("position={:?} value={}", (r, c), value);
            new_image[&Position::new(r, c)] = new_value
        }
    }
    return new_image;
}

fn read(image: &Image, row: isize, col: isize, default: usize) -> usize {
    let mut value = 0;
    for r in row - 1..row + 2 {
        for c in col - 1..col + 2 {
            value <<= 1;
            if r >= 0 && r < image.num_rows as isize && c >= 0 && c < image.num_cols as isize {
                if image[&Position::new(r as usize, c as usize)] {
                    value += 1;
                }
            } else {
                value += default;
            }
        }
    }
    return value;
}

fn display(image: &Image) {
    for r in 0..image.num_rows {
        for c in 0..image.num_cols {
            print!(
                "{}",
                if image[&Position::new(r, c)] {
                    '#'
                } else {
                    '.'
                }
            );
        }
        println!();
    }

    println!("{} by {}", image.num_rows, image.num_cols);
}
