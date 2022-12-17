use std::fmt::{Display, Formatter};

pub fn part1() -> String {
    let wind = include_str!("res/input17.txt").lines().collect::<Vec<_>>()[0].chars().collect::<Vec<_>>();
    let rocks = vec![Rock::Horizontal, Rock::Plus, Rock::Corner, Rock::Vertical, Rock::Box];

    let mut wind_iterator = infinite_iterator(&wind);
    let mut rock_iterator = infinite_iterator(&rocks);

    let mut chamber = Chamber::new();
    let mut rock_count = 0;
    while rock_count < 2022 {
        chamber.drop(rock_iterator.next().unwrap(), &mut wind_iterator);
        rock_count += 1;
    }

    return format!("{:?}", chamber.height);
}

pub fn part2() -> String {
    // let wind = include_str!("res/input17.txt").lines().collect::<Vec<_>>()[0].chars().collect::<Vec<_>>();
    // let rocks = vec![Rock::Horizontal, Rock::Plus, Rock::Corner, Rock::Vertical, Rock::Box];

    // Numbers determined by manually parsing height increase per rocks.len()
    let startup: Vec<usize> = vec![13, 10, 5, 5, 6, 9, 6, 8, 7, 8, 10, 9, 5, 6, 10, 5, 7, 6, 10, 7, 11, 10, 8, 6, 8, 6, 8, 4, 5, 7, 6, 6, 7, 5, 9, 8, 11, 9, 7, 11, 7, 7, 9, 9, 10, 1, 8, 7, 7, 10, 3, 7];
    let repeat: Vec<usize> = vec![7, 8, 10, 7, 7, 11, 7, 6, 4, 9, 5, 6, 9, 11, 9, 9, 5, 9, 8, 4, 5, 8, 10, 7, 11, 11, 6, 10, 9, 11, 13, 7, 8, 8, 6, 6, 12, 9, 7, 6, 9, 8, 7, 7, 8, 6, 5, 6, 10, 4, 7, 8, 8, 9, 6, 7, 9, 8, 8, 7, 4, 7, 7, 10, 8, 3, 4, 9, 7, 9, 9, 9, 6, 10, 9, 8, 7, 7, 5, 10, 8, 6, 7, 7, 8, 11, 11, 9, 8, 5, 9, 11, 9, 3, 7, 9, 2, 4, 9, 4, 7, 8, 10, 7, 8, 8, 11, 6, 4, 6, 6, 8, 7, 7, 8, 10, 9, 7, 8, 6, 9, 6, 6, 9, 6, 5, 10, 8, 11, 3, 6, 6, 4, 13, 6, 7, 8, 6, 5, 5, 8, 4, 6, 6, 8, 8, 7, 13, 7, 8, 10, 6, 9, 7, 6, 9, 7, 7, 10, 10, 6, 7, 9, 9, 7, 9, 8, 8, 9, 8, 7, 6, 8, 7, 9, 6, 7, 8, 7, 9, 8, 4, 7, 6, 9, 9, 5, 9, 7, 7, 5, 7, 13, 12, 8, 8, 4, 12, 6, 8, 7, 9, 3, 7, 7, 8, 3, 7, 9, 7, 9, 4, 6, 7, 7, 10, 9, 5, 7, 9, 10, 10, 10, 7, 12, 6, 8, 5, 6, 6, 11, 10, 9, 10, 11, 5, 7, 10, 6, 9, 7, 6, 4, 9, 9, 9, 8, 7, 8, 8, 7, 13, 8, 9, 6, 5, 9, 8, 11, 9, 8, 11, 9, 8, 9, 8, 6, 7, 7, 10, 5, 7, 5, 11, 5, 9, 7, 4, 3, 5, 12, 6, 6, 9, 7, 9, 9, 7, 9, 7, 7, 6, 9, 7, 6, 6, 12, 8, 11, 3, 10, 11, 12, 6, 5, 7, 5, 6, 10, 5, 6, 7, 7, 5, 10, 8, 8, 10, 7, 8, 10, 9, 7, 6, 5, 7, 7, 7, 9, 11, 9, 6, 7, 9, 6, 7, 9, 8, 7, 7, 6, 9, 11, 11, 11, 8];
    let startup_height: usize = startup.iter().sum();
    let repeat_height: usize = repeat.iter().sum();

    let mut height: usize = 0;
    let mut rocks: usize = 1000000000000;

    rocks -= 5 * startup.len();
    height += startup_height;

    let repeat_count = rocks / (5 * repeat.len());
    rocks = rocks % (5 * repeat.len());
    height += repeat_count * repeat_height;

    if rocks % 5 != 0 {
        panic!();
    }

    let mut repeat_iter = repeat.iter();
    while rocks > 0 {
        let h = repeat_iter.next().unwrap();
        height += h;
        rocks -= 5;
    }

    return format!("{:?}", height);
}

fn infinite_iterator<'a, T>(source: &'a Vec<T>) -> impl Iterator<Item=&'a T> + 'a {
    let mut num = 0;
    return std::iter::from_fn(move || {
        num += 1;
        return Some(&source[(num - 1) % source.len()]);
    });
}

enum Rock {
    Horizontal,
    Plus,
    Corner,
    Vertical,
    Box,
}

impl Rock {
    fn occupies(&self, left: usize, bottom: usize) -> Vec<(usize, usize)> {
        return match self {
            Rock::Horizontal => vec![
                (left, bottom),
                (left + 1, bottom),
                (left + 2, bottom),
                (left + 3, bottom),
            ],
            Rock::Plus => vec![
                (left + 1, bottom),
                (left, bottom + 1),
                (left + 1, bottom + 1),
                (left + 2, bottom + 1),
                (left + 1, bottom + 2),
            ],
            Rock::Corner => vec![
                (left, bottom),
                (left + 1, bottom),
                (left + 2, bottom),
                (left + 2, bottom + 1),
                (left + 2, bottom + 2),
            ],
            Rock::Vertical => vec![
                (left, bottom),
                (left, bottom + 1),
                (left, bottom + 2),
                (left, bottom + 3),
            ],
            Rock::Box => vec![
                (left, bottom),
                (left, bottom + 1),
                (left + 1, bottom),
                (left + 1, bottom + 1),
            ],
        }
    }
}

struct Chamber {
    pub verticals: [Vec<char>; 7],
    pub height: usize,
}

impl Chamber {
    fn new() -> Self {
        Chamber { verticals: Default::default(), height: 0 }
    }

    fn intersects(&mut self, rock: &Rock, left: usize, bottom: usize) -> bool {
        return rock.occupies(left, bottom).iter().any(|(l, b)| l >= &self.verticals.len() || (b < &self.height && &self.verticals[*l][*b] == &'#'));
    }

    fn drop<'a>(&mut self, rock: &Rock, wind: &mut impl Iterator<Item=&'a char>) -> usize {
        let mut left = 2;
        let mut bottom = self.height + 3;

        let mut wind_count = 0;
        loop {
            wind_count += 1;
            match wind.next().unwrap() {
                '<' => {
                    if left > 0 && !self.intersects(rock, left - 1, bottom) {
                        left -= 1;
                    }
                },
                '>' => {
                    if !self.intersects(rock, left + 1, bottom) {
                        left += 1;
                    }
                },
                _ => panic!(),
            }

            if bottom > 0 && !self.intersects(rock, left, bottom - 1) {
                bottom -= 1;
            } else {
                let occupies = rock.occupies(left, bottom);

                // Pad verticals to new height
                self.height = self.height.max(occupies.iter().fold(0, |acc, (_, b)| acc.max(*b)) + 1);
                for v in &mut self.verticals {
                    while v.len() < self.height {
                        v.push('.');
                    }
                }

                for (l, b) in occupies {
                    self.verticals[l][b] = '#';
                }

                return wind_count;
            }
        }
    }
}

impl Display for Chamber {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        writeln!(f, "|.......|")?;
        writeln!(f, "|.......|")?;
        for h in (0..self.height).rev() {
            write!(f, "|")?;
            for v in &self.verticals {
                write!(f, "{}", v[h])?;
            }
            writeln!(f, "|")?;
        }
        return writeln!(f, "+-------+");
    }
}
