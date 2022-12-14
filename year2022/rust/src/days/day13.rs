use std::cmp::Ordering;
use std::str::Chars;

pub fn part1() -> String {
    let items = include_str!("res/input13.txt").lines()
        .filter(|line| line.len() > 0)
        .map(|line| Item::parse(line))
        .collect::<Vec<_>>();

    let mut sum = 0;
    for i in 0..items.len() / 2 {
        if &items[2 * i] <= &items[2 * i + 1] {
            sum += i + 1;
        }
    }

    return format!("{:?}", sum);
}

pub fn part2() -> String {
    let mut items = include_str!("res/input13.txt").lines()
        .filter(|line| line.len() > 0)
        .map(|line| Item::parse(line))
        .collect::<Vec<_>>();
    items.push(Item::parse("[[2]]"));
    items.push(Item::parse("[[6]]"));

    items.sort();

    let divider1 = Item::parse("[[2]]");
    let divider2 = Item::parse("[[6]]");
    let divider_index1 = items.iter().position(|item| item == &divider1).unwrap() + 1;
    let divider_index2 = items.iter().position(|item| item == &divider2).unwrap() + 1;

    return format!("{:?}", divider_index1 * divider_index2);
}

#[derive(Debug, Hash, Eq, PartialEq)]
enum Item {
    Integer(isize),
    List(Vec<Item>),
}

impl Item {
    fn parse(input: &str) -> Item {
        let mut chars = input.chars();
        let (item, _) = Item::parse_recursive(chars.next().unwrap(), &mut chars);
        return item;
    }


    fn parse_recursive(first: char, input: &mut Chars) -> (Item, char) {
        match first {
            '[' => {
                let mut items = Vec::<Item>::new();
                loop {
                    let next = input.next().unwrap();
                    match next {
                        ']' => break, // done with list
                        ',' => continue, // ignore
                        _ => {},
                    }

                    let (item, last) = Item::parse_recursive(next, input);
                    items.push(item);
                    if last == ']' { break }
                }

                return (Item::List(items), '!');
            },
            '0'..='9' => {
                let mut value = String::new();
                value.push(first);

                loop {
                    let next = input.next().unwrap();
                    match next {
                        '0'..='9' => value.push(next),
                        ',' | ']' => return (Item::Integer(value.parse::<isize>().unwrap()), next),
                        _ => panic!(),
                    }
                }
            },
            _ => panic!(),
        }
    }
}

impl PartialOrd<Self> for Item {
    fn partial_cmp(&self, other: &Self) -> Option<Ordering> {
        Some(self.cmp(other))
    }
}

impl Ord for Item {
    fn cmp(&self, other: &Self) -> Ordering {
        match self {
            Item::Integer(self_value) => match other {
                Item::Integer(other_value) => self_value.cmp(&other_value),
                Item::List(_) => Item::List(vec![Item::Integer(*self_value)]).cmp(other),
            },
            Item::List(self_value) => match other {
                Item::Integer(other_value) => self.cmp(&Item::List(vec![Item::Integer(*other_value)])),
                Item::List(other_value) => {
                    for i in 0..self_value.len().min(other_value.len()) {
                        match self_value[i].cmp(&other_value[i]) {
                            Ordering::Less => return Ordering::Less,
                            Ordering::Greater => return Ordering::Greater,
                            Ordering::Equal => {},
                        }
                    }

                    return self_value.len().cmp(&other_value.len());
                },
            },
        }
    }
}
