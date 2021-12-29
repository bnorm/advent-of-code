use std::collections::{HashMap, VecDeque};
use std::collections::hash_map::Entry;
use std::ops::{Index, IndexMut};
use std::slice::Iter;
use itertools::Itertools;

#[derive(Copy, Clone, PartialEq, Eq, Hash, Debug)]
pub enum Variable {
    W,
    X,
    Y,
    Z,
}

impl Variable {
    fn iterator() -> Iter<'static, Variable> {
        static VARIABLES: [Variable; 4] = [Variable::W, Variable::X, Variable::Y, Variable::Z];
        VARIABLES.iter()
    }

    pub fn parse(value: &str) -> Option<Variable> {
        return match value {
            "w" => Some(Variable::W),
            "x" => Some(Variable::X),
            "y" => Some(Variable::Y),
            "z" => Some(Variable::Z),
            _ => None,
        };
    }
}

#[derive(Copy, Clone, PartialEq, Eq, Hash, Debug)]
pub enum Input {
    Variable(Variable),
    Value(isize),
}

impl Input {
    pub fn parse(value: &str) -> Input {
        if let Some(v) = Variable::parse(value) {
            return Input::Variable(v);
        }
        return Input::Value(value.parse::<isize>().unwrap());
    }
}

#[derive(Copy, Clone, PartialEq, Eq, Hash, Debug)]
pub enum Instruction {
    Inp(Variable),
    Add(Variable, Input),
    Mul(Variable, Input),
    Div(Variable, Input),
    Mod(Variable, Input),
    Eql(Variable, Input),
}

impl Instruction {
    pub fn parse(line: &str) -> Instruction {
        let tokens = line.split_whitespace().collect_vec();
        return match tokens[0] {
            "inp" => Instruction::Inp(Variable::parse(tokens[1]).unwrap()),
            "add" => Instruction::Add(Variable::parse(tokens[1]).unwrap(), Input::parse(tokens[2])),
            "mul" => Instruction::Mul(Variable::parse(tokens[1]).unwrap(), Input::parse(tokens[2])),
            "div" => Instruction::Div(Variable::parse(tokens[1]).unwrap(), Input::parse(tokens[2])),
            "mod" => Instruction::Mod(Variable::parse(tokens[1]).unwrap(), Input::parse(tokens[2])),
            "eql" => Instruction::Eql(Variable::parse(tokens[1]).unwrap(), Input::parse(tokens[2])),
            _ => todo!(),
        };
    }
}

#[derive(Clone, Debug)]
pub struct ALU {
    variables: HashMap<Variable, isize>,
    input: VecDeque<isize>,
}

impl ALU {
    pub fn new(input: Vec<isize>) -> Self {
        let mut variables = HashMap::new();
        Variable::iterator().for_each(|v| { variables.insert(*v, 0); });

        let input = VecDeque::from(input);
        Self { variables, input }
    }

    pub fn execute(&mut self, instruction: &Instruction) {
        match instruction {
            Instruction::Inp(v) => {
                self[v] = self.input.pop_front().expect("no more input");
            }
            Instruction::Add(v, i) => {
                self[v] += self.read_input(i);
            }
            Instruction::Mul(v, i) => {
                self[v] *= self.read_input(i);
            }
            Instruction::Div(v, i) => {
                self[v] /= self.read_input(i);
            }
            Instruction::Mod(v, i) => {
                self[v] %= self.read_input(i);
            }
            Instruction::Eql(v, i) => {
                self[v] = if self[v] == self.read_input(i) { 1 } else { 0 };
            }
        }
    }

    fn read_input(&self, input: &Input) -> isize {
        return match input {
            Input::Variable(variable) => self.variables[variable],
            Input::Value(value) => *value,
        };
    }
}

impl Index<&Variable> for ALU {
    type Output = isize;

    fn index(&self, variable: &Variable) -> &Self::Output {
        return &self.variables.get(variable).unwrap_or(&0);
    }
}

impl IndexMut<&Variable> for ALU {
    fn index_mut(&mut self, variable: &Variable) -> &mut Self::Output {
        return match self.variables.entry(*variable) {
            Entry::Occupied(o) => o.into_mut(),
            Entry::Vacant(v) => v.insert(0)
        };
    }
}
