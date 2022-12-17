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
    let wind = include_str!("res/input17.txt").lines().collect::<Vec<_>>()[0].chars().collect::<Vec<_>>();
    let rocks = vec![Rock::Horizontal, Rock::Plus, Rock::Corner, Rock::Vertical, Rock::Box];

    let (startup, cycle) = find_cycle(&rocks, &wind);

    let mut height = 0;
    let mut rock_count = 1000000000000;

    rock_count -= rocks.len() * startup.len();
    height += startup.iter().sum::<usize>();

    let cycle_count = rock_count / (rocks.len() * cycle.len());
    rock_count = rock_count % (rocks.len() * cycle.len());
    height += cycle_count * cycle.iter().sum::<usize>();

    let mut cycle_iter = cycle.iter();
    while rock_count > 0 {
        height += cycle_iter.next().unwrap();
        rock_count -= rocks.len();
    }

    return format!("{:?}", height);
}

fn find_cycle(rocks: &Vec<Rock>, wind: &Vec<char>) -> (Vec<usize>, Vec<usize>) {
    let mut wind_iterator = infinite_iterator(&wind);
    let mut rock_iterator = infinite_iterator(&rocks);

    let mut rock_cycle_heights = Vec::<usize>::new();

    let mut startup = Vec::<usize>::new();
    let mut cycle = Vec::<usize>::new();

    let mut chamber = Chamber::new();
    let mut rock_count = 0;
    let mut last_height = 0;
    loop {
        chamber.drop(rock_iterator.next().unwrap(), &mut wind_iterator);
        rock_count += 1;
        if rock_count % rocks.len() == 0 {
            rock_cycle_heights.push(chamber.height - last_height);
            last_height = chamber.height;

            // Guess that the cycle must be greater in size than 10 (trial and error...)
            for repeat_size in 10..rock_cycle_heights.len() / 2 {
                let (left, right) = rock_cycle_heights.split_at(rock_cycle_heights.len() - repeat_size);
                if left.ends_with(right) {
                    for r in right {
                        cycle.push(*r);
                    }
                    for i in 0..left.len() - right.len() {
                        startup.push(left[i]);
                    }
                    return (startup, cycle);
                }
            }
        }
    }
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
