advent_of_code::solution!(1);

pub struct Instruction {
    pub direction: char,
    pub clicks: i64,
}

pub fn part_one(input: &str) -> Option<u64> {
    let instructions = input
        .lines()
        .map(|line| Instruction {
            direction: line.chars().next().unwrap(),
            clicks: line.get(1..).unwrap().parse().unwrap(),
        })
        .collect::<Vec<_>>();

    let mut dial: i64 = 50;
    let mut count: u64 = 0;
    for instruction in instructions {
        let clicks = match instruction.direction {
            'R' => instruction.clicks,
            'L' => -instruction.clicks,
            _ => unreachable!(),
        };

        dial += clicks;
        if dial % 100 == 0 {
            count += 1;
        }
    }

    Some(count)
}

pub fn part_two(input: &str) -> Option<u64> {
    let instructions = input
        .lines()
        .map(|line| Instruction {
            direction: line.chars().next().unwrap(),
            clicks: line.get(1..).unwrap().parse().unwrap(),
        })
        .collect::<Vec<_>>();

    let mut dial: i64 = 50;
    let mut count: u64 = 0;
    for instruction in instructions {
        let direction = match instruction.direction {
            'R' => 1,
            'L' => -1,
            _ => unreachable!(),
        };

        for _ in 0..instruction.clicks {
            dial += direction;
            if dial % 100 == 0 {
                count += 1;
            }
        }
    }

    Some(count)
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_part_one() {
        let result = part_one(&advent_of_code::template::read_file("examples", DAY));
        assert_eq!(result, Some(3));
    }

    #[test]
    fn test_part_two() {
        let result = part_two(&advent_of_code::template::read_file("examples", DAY));
        assert_eq!(result, Some(6));
    }
}
