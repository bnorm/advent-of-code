use recap::Recap;
use serde::Deserialize;

pub fn part1() -> String {
    let input = include_str!("res/input02.txt").lines().collect::<Vec<_>>();
    let games = input.iter().map(|line| { line.parse::<Game>().unwrap() }).collect::<Vec<_>>();
    let score: i32 = games.iter().filter_map(|game| { game.score_part1() }).sum();
    return format!("{:?}", score);
}

pub fn part2() -> String {
    let input = include_str!("res/input02.txt").lines().collect::<Vec<_>>();
    let games = input.iter().map(|line| { line.parse::<Game>().unwrap() }).collect::<Vec<_>>();
    let score: i32 = games.iter().filter_map(|game| { game.score_part2() }).sum();
    return format!("{:?}", score);
}

#[derive(Debug, Copy, Clone, Hash, Eq, PartialEq, Deserialize, Recap)]
#[recap(regex = r#"(?P<them>[A-C]) (?P<choice>[X-Z])"#)]
pub struct Game {
    pub them: char,
    pub choice: char,
}

impl Game {
    fn score_part1(&self) -> Option<i32> {
        match self.them {
            // A, X -> Rock: 1
            // B, Y -> Paper: 2
            // C, Z -> Scissors: 3

            'A' => match self.choice {
                'X' => Some(1 + 3),
                'Y' => Some(2 + 6),
                'Z' => Some(3 + 0),
                _ => None,
            },
            'B' => match self.choice {
                'X' => Some(1 + 0),
                'Y' => Some(2 + 3),
                'Z' => Some(3 + 6),
                _ => None,
            },
            'C' => match self.choice {
                'X' => Some(1 + 6),
                'Y' => Some(2 + 0),
                'Z' => Some(3 + 3),
                _ => None,
            },
            _ => None,
        }
    }

    fn score_part2(&self) -> Option<i32> {
        match self.them {
            // A -> Rock: 1
            // B -> Paper: 2
            // C -> Scissors: 3

            // X -> Lose: 0
            // Y -> Draw: 3
            // Z -> Win: 6

            'A' => match self.choice {
                'X' => Some(0 + 3),
                'Y' => Some(3 + 1),
                'Z' => Some(6 + 2),
                _ => None,
            },
            'B' => match self.choice {
                'X' => Some(0 + 1),
                'Y' => Some(3 + 2),
                'Z' => Some(6 + 3),
                _ => None,
            },
            'C' => match self.choice {
                'X' => Some(0 + 2),
                'Y' => Some(3 + 3),
                'Z' => Some(6 + 1),
                _ => None,
            },
            _ => None,
        }
    }
}
