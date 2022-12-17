use std::collections::HashMap;
use std::hash::Hash;
use std::str::FromStr;

use recap::Recap;
use serde::Deserialize;

pub fn part1() -> String {
    let mut valves = include_str!("res/input16.txt").lines()
        .map(|line| line.parse::<Valve>().unwrap())
        .collect::<Vec<_>>();

    valves.sort_by(|a, b| b.flow_rate.cmp(&a.flow_rate)); // reverse sort by flow rate
    let valve_indexes = valves.iter().enumerate().map(|(i, v)| (&v.name, i)).collect::<HashMap<_, _>>();
    let valve_tunnel_indexes = valves.iter()
        .map(|v| v.tunnels.iter().map(|n| valve_indexes[n]).collect::<Vec<_>>())
        .collect::<Vec<_>>();

    let positive_flow = valves.iter()
        .filter(|v| v.flow_rate > 0)
        .count();
    let permutation_count = 1 << positive_flow;

    let memory = build_memory(30, &valves, &valve_tunnel_indexes, permutation_count);
    let key = state_index(permutation_count - 1, permutation_count, valve_indexes[&"AA".to_string()], valves.len(), 29);
    return format!("{:?}", memory[key]);
}

pub fn part2() -> String {
    let mut valves = include_str!("res/input16.txt").lines()
        .map(|line| line.parse::<Valve>().unwrap())
        .collect::<Vec<_>>();

    valves.sort_by(|a, b| b.flow_rate.cmp(&a.flow_rate)); // reverse sort by flow rate
    let valve_indexes = valves.iter().enumerate().map(|(i, v)| (&v.name, i)).collect::<HashMap<_, _>>();
    let valve_tunnel_indexes = valves.iter()
        .map(|v| v.tunnels.iter().map(|n| valve_indexes[n]).collect::<Vec<_>>())
        .collect::<Vec<_>>();

    let positive_flow = valves.iter()
        .filter(|v| v.flow_rate > 0)
        .count();
    let permutation_count = 1 << positive_flow;

    let memory = build_memory(26, &valves, &valve_tunnel_indexes, permutation_count);

    // You and the elephant have disjoint sets of unopened valves
    let start_valve_index = valve_indexes[&"AA".to_string()];
    let mut best = 0;
    for you_bit_mask in 0..permutation_count / 2 {
        let elephant_bit_mask = permutation_count - 1 - you_bit_mask;
        let you_start_key = state_index(you_bit_mask, permutation_count, start_valve_index, valves.len(), 25);
        let elephant_start_key = state_index(elephant_bit_mask, permutation_count, start_valve_index, valves.len(), 25);
        best = best.max(memory[you_start_key] + memory[elephant_start_key])
    }

    return format!("{:?}", best);
}

fn build_memory(max_clock: usize, valves: &Vec<Valve>, valve_tunnel_indexes: &Vec<Vec<usize>>, permutation_count: usize) -> Vec<usize> {
    let mut memory = vec![0; max_clock * valves.len() * permutation_count];

    for clock in 1..max_clock {
        for current_valve_index in 0..valves.len() {
            // println!("clock={:?} current_valve_index={:?}", clock, current_valve_index);
            let valve = &valves[current_valve_index];
            let current_valve_bit_mask = 1 << current_valve_index;
            for open_valves_bit_mask in 0..permutation_count {
                let mut released = 0;
                if clock > 1 && open_valves_bit_mask & current_valve_bit_mask != 0 {
                    let key = state_index(open_valves_bit_mask - current_valve_bit_mask, permutation_count, current_valve_index, valves.len(), clock - 1);
                    released = released.max(memory[key] + valve.flow_rate * clock);
                }
                for previous_valve_index in &valve_tunnel_indexes[current_valve_index] {
                    let key = state_index(open_valves_bit_mask, permutation_count, *previous_valve_index, valves.len(), clock - 1);
                    released = released.max(memory[key]);
                }
                let key = state_index(open_valves_bit_mask, permutation_count, current_valve_index, valves.len(), clock);
                memory[key] = released;
            }
        }
    }

    return memory;
}

#[derive(Debug, Deserialize, Recap)]
#[recap(regex = r#"Valve (?P<name>[A-Z]+) has flow rate=(?P<flow_rate>\d+); tunnels? leads? to valves? (?P<tunnels>[A-Z, ]+)"#)]
struct Input {
    pub name: String,
    pub flow_rate: usize,
    pub tunnels: String,
}

#[derive(Debug, Clone, Hash, Eq, PartialEq)]
struct Valve {
    pub name: String,
    pub flow_rate: usize,
    pub tunnels: Vec<String>,
}

impl FromStr for Valve {
    type Err = recap::Error;

    fn from_str(s: &str) -> Result<Self, Self::Err> {
        let input = s.parse::<Input>()?;
        return Ok(Valve {
            name: input.name,
            flow_rate: input.flow_rate,
            tunnels: input.tunnels.split(", ").map(|name| name.to_string()).collect(),
        });
    }
}

fn state_index(
    // bit mask of all open valves
    opened: usize,
    permutation_count: usize,
    // index of current valve
    current: usize,
    valve_count: usize,
    // clock value 1..=29
    clock: usize,
) -> usize {
    let mut index = clock;
    index = index * valve_count + current;
    index = index * permutation_count + opened;
    return index;
}
