use std::str::FromStr;

pub fn part1() -> String {
    let operations = include_str!("res/input10.txt").lines()
        .filter_map(|line| line.parse::<Operation>().ok())
        .collect::<Vec<_>>();


    let mut answer: isize = 0;

    let mut clock: isize = 1;
    let mut registry: isize = 1;
    for op in operations {
        let mut cycle = || {
            // println!("cycle: {} registry: {}, op: {:?}", clock, registry, op);
            if (clock + 20) % 40 == 0 {
                answer += clock * registry;
                // println!("cycle: {} registry: {}, op: {:?}", clock, registry, op);
            }
            clock += 1;
        };

        match op {
            Operation::Noop => {
                cycle();
            }
            Operation::Addx(value) => {
                cycle();
                cycle();
                registry += value;
            }
        }
    }

    return format!("{:?}", answer);
}

pub fn part2() -> String {
    let operations = include_str!("res/input10.txt").lines()
        .filter_map(|line| line.parse::<Operation>().ok())
        .collect::<Vec<_>>();


    let mut clock: isize = 1;
    let mut registry: isize = 1;
    for op in operations {
        let mut cycle = || {
            let crt = (clock - 1) % 40;
            if registry - 1 <= crt && crt <= registry + 1 {
                print!("#");
            } else {
                print!(".");
            }
            if clock % 40 == 0 {
                println!();
            }
            clock += 1;
        };

        match op {
            Operation::Noop => {
                cycle();
            }
            Operation::Addx(value) => {
                cycle();
                cycle();
                registry += value;
            }
        }
    }

    return format!("{:?}", 0);
}

#[derive(Debug)]
enum Operation {
    Noop,
    Addx(isize),
}

struct ParserError {}

impl From<std::num::ParseIntError> for ParserError {
    fn from(_: std::num::ParseIntError) -> ParserError {
        ParserError {}
    }
}

impl FromStr for Operation {
    type Err = ParserError;

    fn from_str(s: &str) -> Result<Self, Self::Err> {
        let split = s.split(" ").collect::<Vec<_>>();
        return match split[0] {
            "noop" => return Ok(Operation::Noop),
            "addx" => {
                let value = split[1].parse::<isize>()?;
                return Ok(Operation::Addx(value))
            },
            _ => Err(ParserError {}),
        };
    }
}
