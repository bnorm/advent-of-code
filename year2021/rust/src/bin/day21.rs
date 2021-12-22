use std::collections::hash_map::Entry;
use std::collections::HashMap;
use std::fmt::{Debug, Formatter};
use std::fs;
use std::hash::Hash;
use std::time::Instant;

use itertools::Itertools;
use recap::Recap;
use serde::Deserialize;

#[derive(Debug, PartialEq, Eq, Hash, Deserialize, Recap)]
#[recap(regex = r#"Player (?P<id>\d+) starting position: (?P<start>\d+)"#)]
struct PlayerStart {
    id: usize,
    start: usize,
}

type Input = Vec<PlayerStart>;

#[derive(Debug, Copy, Clone, PartialEq, Eq, Hash)]
struct Dice {
    value: usize,
    roll_count: usize,
}

impl Dice {
    fn new() -> Dice { return Dice { value: 1, roll_count: 0 }; }

    fn roll(&mut self) -> usize {
        let result = self.value;
        self.roll_count += 1;
        self.value += 1;
        if self.value > 100 { self.value = 1; }
        return result;
    }
}

#[derive(Copy, Clone, PartialEq, Eq, Hash)]
struct Player {
    id: usize,
    location: usize,
    score: usize,
}

impl Debug for Player {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        write!(f, "({}:{}:{})", self.id, self.location, self.score)
    }
}

impl Player {
    fn new(player: &PlayerStart) -> Player {
        return Player { id: player.id, location: player.start, score: 0 };
    }

    fn turn(&mut self, die: &mut Dice) -> bool {
        let roll1 = die.roll();
        let roll2 = die.roll();
        let roll3 = die.roll();
        let mut new_location = self.location + roll1 + roll2 + roll3;
        while new_location > 10 { new_location -= 10 }
        self.score += new_location;
        self.location = new_location;
        // println!("Player {} rolls {}+{}+{} and moves to space {} for a total score of {}", self.id, roll1, roll2, roll3, self.location, self.score);
        return self.score >= 1000;
    }

    fn turn_q(&self, die_roll: usize) -> Player {
        let mut new_location = self.location + die_roll;
        while new_location > 10 { new_location -= 10 }
        return Player { id: self.id, location: new_location, score: self.score + new_location };
    }
}

fn main() {
    let start = Instant::now();
    let input = read_input();
    println!("[part1] answer={:?}", part1(&input));
    println!("[part2] answer={:?}", part2(&input));
    println!("finished in {:?}", start.elapsed());
}

fn read_input() -> Input {
    let filename = "res/input21.txt";
    let contents = fs::read_to_string(filename).unwrap();
    return contents.lines()
        .filter_map(|line| line.parse::<PlayerStart>().ok())
        .collect_vec();
}

fn part1(input: &Input) -> Option<usize> {
    let instant = Instant::now();

    let mut die = Dice::new();
    let mut players = Vec::<Player>::new();
    for player in input {
        // println!("player={:?}", player);
        players.push(Player::new(player));
    }

    let mut result;
    loop {
        let mut winner = false;
        for player in &mut players {
            winner = player.turn(&mut die);
            if winner { break; }
        }
        if winner {
            let loser = players.iter().filter(|p| p.score < 1000).next().unwrap();
            // println!("loser score={} rolls={} ", loser.score, die.roll_count);
            result = die.roll_count * loser.score;
            break;
        }
    }

    println!("[part1] time={:?}", instant.elapsed());
    return Some(result);
}

fn part2(input: &Input) -> Option<usize> {
    let instant = Instant::now();

    // 3: 1
    // 4: 3
    // 5: 6
    // 6: 7
    // 7: 6
    // 8: 3
    // 9: 1
    let mut roll_possibilities = HashMap::<usize, u128>::new();
    for roll1 in 1..4 {
        for roll2 in 1..4 {
            for roll3 in 1..4 {
                increment(&mut roll_possibilities, roll1 + roll2 + roll3, 1);
            }
        }
    }

    let mut universe = HashMap::<Vec<Player>, u128>::new();
    let players = input.iter().map(|p| Player::new(p)).collect_vec();
    let player_count = players.len();
    universe.insert(players, 1);

    let mut results = HashMap::<usize, u128>::new();
    let mut player_index = 0;
    // let mut count = 0;
    while !universe.is_empty() {
        let mut new_universe = HashMap::<Vec<Player>, u128>::new();
        for (player_state, count) in &universe {
            for (roll, multiplier) in &roll_possibilities {
                let new_player = player_state[player_index].turn_q(*roll);
                if new_player.score >= 21 {
                    increment(&mut results, new_player.id, multiplier * count);
                } else {
                    let mut new_player_state = player_state.clone();
                    new_player_state[player_index] = new_player;
                    increment(&mut new_universe, new_player_state, multiplier * count);
                }
            }
        }

        // if count < 4 {
        //     let mut player_universe = HashMap::<Player, u128>::new();
        //     for (player_state, count) in &new_universe {
        //         for p in player_state {
        //             increment(&mut player_universe, p.clone(), *count);
        //         }
        //     }
        //     println!("player={} universe={:?}", player_index, player_universe)
        // }

        universe = new_universe;
        player_index = (player_index + 1) % player_count;
        // count += 1;
    }
    println!("results={:?}", results);

    println!("[part2] time={:?}", instant.elapsed());
    return None;
}

fn increment<T>(results: &mut HashMap<T, u128>, id: T, count: u128) where T: Eq + Hash {
    let value = match results.entry(id) {
        Entry::Occupied(o) => o.into_mut(),
        Entry::Vacant(v) => v.insert(0)
    };
    *value += count;
}
