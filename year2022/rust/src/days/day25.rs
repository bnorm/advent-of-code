pub fn part1() -> String {
    let sum = include_str!("res/input25.txt").lines()
        .map(|line| decode(line))
        .sum::<isize>();
    return format!("{:?}", encode(sum));
}

pub fn part2() -> String {
    let _input = include_str!("res/input25.txt").lines().collect::<Vec<_>>();
    return format!("{:?}", 0);
}

fn decode(input: &str) -> isize {
    let mut value = 0;
    let mut multiple = 1;
    for digit in input.chars().rev() {
        value += multiple * match digit {
            '=' => -2,
            '-' => -1,
            '0' => 0,
            '1' => 1,
            '2' => 2,
            _ => 0,
        };
        multiple *= 5;
    }
    return value;
}

fn encode(input: isize) -> String {
    let mut value = input;
    let mut digits = Vec::<String>::new();
    while value > 0 {
        let digit = value % 5;
        match digit {
            0..=2 => {
                digits.push(digit.to_string());
                value = value / 5;
            },
            3 => {
                digits.push("=".to_string());
                value = value / 5 + 1;
            },
            4 => {
                digits.push("-".to_string());
                value = value / 5 + 1;
            },
            _ => unreachable!()
        };
    }

    digits.reverse();
    return digits.join("");
}
