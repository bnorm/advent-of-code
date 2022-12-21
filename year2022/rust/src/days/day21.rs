use std::collections::{HashMap, VecDeque};
use std::str::FromStr;

use recap::Recap;
use serde::Deserialize;

pub fn part1() -> String {
    let monkeys = include_str!("res/input21.txt").lines()
        .map(|line| line.parse::<Monkey>().unwrap())
        .collect::<Vec<_>>();

    // Forward calculate values and get 'root'
    let values = forward_calculate(&monkeys);
    let root = values.get(&"root".to_string()).unwrap();
    return format!("{:?}", root);
}

pub fn part2() -> String {
    let monkeys = include_str!("res/input21.txt").lines()
        .map(|line| line.parse::<Monkey>().unwrap())
        .filter(|monkey| {
            // Exclude the 'humn' monkey
            if let Monkey::Number { name, .. } = monkey { name != &"humn".to_string() } else { true }
        })
        .collect::<Vec<_>>();

    // Forward calculate all possible values, clone Math monkeys, and add Number monkeys for each calculated value
    let values = forward_calculate(&monkeys);
    let mut new_monkeys = monkeys.clone().into_iter().filter(|m| if let Monkey::Number { .. } = m { false } else { true }).collect::<Vec<_>>();
    for (&name, &value) in &values {
        new_monkeys.push(Monkey::Number { name: name.clone(), value });
    }

    // Add a Number monkey for the opposite side of the root monkey
    for monkey in &monkeys {
        if let Monkey::Math { name, left, right, op: _ } = monkey {
            if name == &"root".to_string() {
                if let Some(&value) = values.get(left) {
                    new_monkeys.push(Monkey::Number { name: right.clone(), value });
                } else if let Some(&value) = values.get(right) {
                    new_monkeys.push(Monkey::Number { name: left.clone(), value });
                }
                break
            }
        }
    }

    // "Backward" calculate values and get 'humn'
    let values = backwards_calculate(&new_monkeys);
    let humn = values.get(&"humn".to_string()).unwrap();
    return format!("{:?}", humn);
}

fn forward_calculate(monkeys: &Vec<Monkey>) -> HashMap<&String, isize> {
    let mut values = HashMap::<&String, isize>::new();
    let mut queue = VecDeque::<&Monkey>::new();
    for m in monkeys {
        queue.push_back(m);
    }

    let mut last_size = 0;
    let mut i = 0;
    while let Some(m) = queue.pop_front() {
        // Infinite loop detection
        i += 1;
        if i % monkeys.len() == 0 {
            if values.len() == last_size {
                return values;
            } else {
                last_size = values.len();
            }
        }

        match m {
            Monkey::Number { name, value } => {
                values.insert(name, *value);
            },
            Monkey::Math { name, left, right, op } => {
                let left_value = if let Some(&it) = values.get(left) { it } else {
                    queue.push_back(m);
                    continue;
                };
                let right_value = if let Some(&it) = values.get(right) { it } else {
                    queue.push_back(m);
                    continue;
                };

                let result = match &op {
                    MathOp::Plus => left_value + right_value,
                    MathOp::Minus => left_value - right_value,
                    MathOp::Multiply => left_value * right_value,
                    MathOp::Divide => left_value / right_value,
                };

                values.insert(name, result);
            }
        }
    }

    return values;
}

fn backwards_calculate(monkeys: &Vec<Monkey>) -> HashMap<&String, isize> {
    let mut values = HashMap::<&String, isize>::new();
    let mut queue = VecDeque::<&Monkey>::new();
    for m in monkeys {
        queue.push_back(m);
    }

    let mut last_size = 0;
    let mut i = 0;
    while let Some(m) = queue.pop_front() {
        // Infinite loop detection
        i += 1;
        if i % monkeys.len() == 0 {
            if values.len() == last_size {
                return values; // Infinite loop
            } else {
                last_size = values.len();
            }
        }

        match m {
            Monkey::Number { name, value } => {
                values.insert(name, *value);
            },
            Monkey::Math { name, left, right, op } => {
                let result_value = if let Some(&it) = values.get(name) { it } else {
                    queue.push_back(m);
                    continue;
                };

                if let Some(&left_value) = values.get(left) {
                    let result = match &op {
                        MathOp::Plus => result_value - left_value,
                        MathOp::Minus => left_value - result_value,
                        MathOp::Multiply => result_value / left_value,
                        MathOp::Divide => left_value / result_value,
                    };

                    values.insert(right, result);
                } else if let Some(&right_value) = values.get(right) {
                    let result = match &op {
                        MathOp::Plus => result_value - right_value,
                        MathOp::Minus => result_value + right_value,
                        MathOp::Multiply => result_value / right_value,
                        MathOp::Divide => result_value * right_value,
                    };

                    values.insert(left, result);
                } else {
                    queue.push_back(m);
                }
            }
        }
    }

    return values;
}

#[derive(Debug, Clone)]
enum Monkey {
    Number {
        name: String,
        value: isize,
    },
    Math {
        name: String,
        left: String,
        right: String,
        op: MathOp,
    },
}

impl FromStr for Monkey {
    type Err = recap::Error;

    fn from_str(s: &str) -> Result<Self, Self::Err> {
        return if let Ok(it) = s.parse::<NumberInput>() {
            Ok(Monkey::Number { name: it.name, value: it.value })
        } else {
            let it = s.parse::<MathInput>()?;
            Ok(Monkey::Math { name: it.name, left: it.left, right: it.right, op: it.op })
        }
    }
}

#[derive(Deserialize, Recap)]
#[recap(regex = r#"(?P<name>[a-z]+): (?P<value>\d+)"#)]
struct NumberInput {
    pub name: String,
    pub value: isize,
}

#[derive(Deserialize, Recap)]
#[recap(regex = r#"(?P<name>[a-z]+): (?P<left>[a-z]+) (?P<op>[-+*/]) (?P<right>[a-z]+)"#)]
struct MathInput {
    pub name: String,
    pub left: String,
    pub right: String,
    pub op: MathOp,
}

#[derive(Debug, Clone, Deserialize)]
enum MathOp {
    #[serde(rename = "+")]
    Plus,
    #[serde(rename = "-")]
    Minus,
    #[serde(rename = "*")]
    Multiply,
    #[serde(rename = "/")]
    Divide,
}
