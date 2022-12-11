use std::collections::VecDeque;
use std::fmt;

use recap::Recap;
use serde::Deserialize;

pub fn part1() -> String {
    let mut monkeys = include_str!("res/input11.txt")
        .split("\n\n")
        .filter_map(|section| section.parse::<MonkeyInput>().ok())
        .map(|input| Monkey::new(input))
        .collect::<Vec<_>>();

    for _round in 0..20 {
        for id in 0..monkeys.len() {
            let items = monkeys[id].items.drain(..).collect::<Vec<_>>();
            monkeys[id].inspection_count += items.len();
            for old_item in items {
                let new_item = monkeys[id].operation(old_item) / 3;
                let new_id = monkeys[id].test(new_item);
                monkeys[new_id].items.push_back(new_item);
            }
        }
        // println!("round: {}, monkeys: {:?}", _round + 1, monkeys)
    }

    monkeys.sort_by(|m1, m2| m2.inspection_count.cmp(&m1.inspection_count));
    let business = monkeys[0].inspection_count * monkeys[1].inspection_count;

    return format!("{:?}", business);
}

pub fn part2() -> String {
    let mut monkeys = include_str!("res/input11.txt")
        .split("\n\n")
        .filter_map(|section| section.parse::<MonkeyInput>().ok())
        .map(|input| Monkey::new(input))
        .collect::<Vec<_>>();

    let mut gcd = 1;
    for m in 0..monkeys.len() {
        gcd *= monkeys[m].input.test;
    }

    for _round in 0..10_000 {
        for id in 0..monkeys.len() {
            let items = monkeys[id].items.drain(..).collect::<Vec<_>>();
            monkeys[id].inspection_count += items.len();
            for old_item in items {
                let new_item = monkeys[id].operation(old_item) % gcd;
                let new_id = monkeys[id].test(new_item);
                monkeys[new_id].items.push_back(new_item);
            }
        }
        // println!("round: {}, monkeys: {:?}", _round + 1, monkeys);
    }

    monkeys.sort_by(|m1, m2| m2.inspection_count.cmp(&m1.inspection_count));
    let business = monkeys[0].inspection_count * monkeys[1].inspection_count;

    return format!("{:?}", business);
}

#[derive(Debug, Deserialize, Recap)]
#[recap(regex = r#"Monkey (?P<id>\d+):
  Starting items: (?P<items>[\d, ]+)
  Operation: new = old (?P<operation>[old\d *+]+)
  Test: divisible by (?P<test>[\d]+)
    If true: throw to monkey (?P<if_true>[\d]+)
    If false: throw to monkey (?P<if_false>[\d]+)"#)]
struct MonkeyInput {
    id: usize,
    items: String,
    operation: String,
    test: usize,
    if_true: usize,
    if_false: usize,
}

struct Monkey {
    pub id: usize,
    pub inspection_count: usize,
    pub items: VecDeque<usize>,
    input: MonkeyInput,
}

impl Monkey {
    fn new(input: MonkeyInput) -> Self {
        return Self {
            id: input.id,
            inspection_count: 0,
            items: VecDeque::from_iter(input.items.split(", ").filter_map(|item| item.parse::<usize>().ok())),
            input,
        };
    }

    fn operation(&self, old: usize) -> usize {
        let parts = self.input.operation.split(" ").collect::<Vec<_>>();
        let value = if parts[1] == "old" {
            old
        } else {
            parts[1].parse::<usize>().unwrap()
        };

        if parts[0] == "*" {
            return old * value;
        } else if parts[0] == "+" {
            return old + value;
        } else {
            panic!("{:?}", self.input.operation);
        }
    }

    fn test(&self, item: usize) -> usize {
        return if item % self.input.test == 0 {
            self.input.if_true
        } else {
            self.input.if_false
        }
    }
}

impl fmt::Debug for Monkey {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.debug_struct("Monkey")
            .field("id", &self.id)
            .field("inspection_count", &self.inspection_count)
            .field("items", &self.items)
            .finish()
    }
}
