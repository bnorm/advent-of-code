use recap::Recap;
use serde::Deserialize;

pub fn part1() -> String {
    let quality = include_str!("res/input19.txt").lines()
        .map(|line| line.parse::<Blueprint>().unwrap())
        .map(|blueprint| blueprint.id * blueprint.max_geodes(24))
        .sum::<usize>();
    return format!("{:?}", quality);
}

pub fn part2() -> String {
    let quality = include_str!("res/input19.txt").lines()
        .take(3)
        .map(|line| line.parse::<Blueprint>().unwrap())
        .map(|blueprint| blueprint.max_geodes(32))
        .fold(1 , |acc, geodes| acc * geodes);
    return format!("{:?}", quality);
}

#[derive(Debug, Copy, Clone, Ord, PartialOrd, Hash, Eq, PartialEq, Deserialize, Recap)]
#[recap(regex = r#"Blueprint (?P<id>\d+): Each ore robot costs (?P<ore_robot_ore_cost>\d+) ore. Each clay robot costs (?P<clay_robot_ore_cost>\d+) ore. Each obsidian robot costs (?P<obsidian_robot_ore_cost>\d+) ore and (?P<obsidian_robot_clay_cost>\d+) clay. Each geode robot costs (?P<geode_robot_ore_cost>\d+) ore and (?P<geode_robot_obsidian_cost>\d+) obsidian."#)]
struct Blueprint {
    pub id: usize,
    pub ore_robot_ore_cost: usize,
    pub clay_robot_ore_cost: usize,
    pub obsidian_robot_ore_cost: usize,
    pub obsidian_robot_clay_cost: usize,
    pub geode_robot_ore_cost: usize,
    pub geode_robot_obsidian_cost: usize,
}

#[derive(PartialEq)]
enum RobotType {
    ORE,
    CLAY,
    OBSIDIAN,
    GEODE,
}

impl Blueprint {
    fn max_geodes(&self, time_available: usize) -> usize {
        fn recursion(
            max_time: usize,
            blueprint: &Blueprint,
            state: &State,
            next_robot: &RobotType,
            max_ore_cost: usize,
            max_clay_cost: usize,
            max_obsidian_cost: usize,
        ) -> usize {
            let mut next = state.clone();
            while !next.can_build(&next_robot, blueprint) {
                next.minute += 1;
                next.gather_resources();
                if next.minute == max_time {
                    return next.geodes;
                }
            }

            next.minute += 1;
            next.gather_resources();
            next.build_robot(&next_robot, blueprint);
            if next.minute == max_time {
                return next.geodes;
            }

            let mut best = next.geodes;
            if state.obsidian_robots > 0 {
                best = best.max(recursion(max_time, blueprint, &next, &RobotType::GEODE, max_ore_cost, max_clay_cost, max_obsidian_cost));
            }
            if state.obsidian_robots < max_obsidian_cost && state.clay_robots > 0 {
                best = best.max(recursion(max_time, blueprint, &next, &RobotType::OBSIDIAN, max_ore_cost, max_clay_cost, max_obsidian_cost));
            }
            if state.clay_robots < max_clay_cost {
                best = best.max(recursion(max_time, blueprint, &next, &RobotType::CLAY, max_ore_cost, max_clay_cost, max_obsidian_cost));
            }
            if state.ore_robots < max_ore_cost {
                best = best.max(recursion(max_time, blueprint, &next, &RobotType::ORE, max_ore_cost, max_clay_cost, max_obsidian_cost));
            }
            return best;
        }

        let state = State::initial();
        let mut best = 0;
        for robot_type in [RobotType::ORE, RobotType::CLAY] {
            best = best.max(recursion(
                time_available,
                self,
                &state,
                &robot_type,
                self.ore_robot_ore_cost.max(self.clay_robot_ore_cost).max(self.obsidian_robot_ore_cost).max(self.geode_robot_ore_cost),
                self.obsidian_robot_clay_cost,
                self.obsidian_robot_clay_cost,
            ));
        }

        return best;
    }
}

#[derive(Debug, Copy, Clone, Hash, Eq, PartialEq)]
struct State {
    pub minute: usize,
    pub ore: usize,
    pub ore_robots: usize,
    pub clay: usize,
    pub clay_robots: usize,
    pub obsidian: usize,
    pub obsidian_robots: usize,
    pub geodes: usize,
    pub geode_robots: usize,
}

impl State {
    fn initial() -> Self {
        return State {
            minute: 0,
            ore: 0,
            ore_robots: 1,
            clay: 0,
            clay_robots: 0,
            obsidian: 0,
            obsidian_robots: 0,
            geodes: 0,
            geode_robots: 0,
        };
    }

    fn gather_resources(&mut self) {
        self.ore += self.ore_robots;
        self.clay += self.clay_robots;
        self.obsidian += self.obsidian_robots;
        self.geodes += self.geode_robots;
    }

    fn can_build(&self, robot_type: &RobotType, blueprint: &Blueprint) -> bool {
        match robot_type {
            RobotType::ORE => self.ore >= blueprint.ore_robot_ore_cost,
            RobotType::CLAY => self.ore >= blueprint.clay_robot_ore_cost,
            RobotType::OBSIDIAN => self.ore >= blueprint.obsidian_robot_ore_cost && self.clay >= blueprint.obsidian_robot_clay_cost,
            RobotType::GEODE => self.ore >= blueprint.geode_robot_ore_cost && self.obsidian >= blueprint.geode_robot_obsidian_cost,
        }
    }

    fn build_robot(&mut self, robot_type: &RobotType, blueprint: &Blueprint) {
        match robot_type {
            RobotType::ORE => {
                self.ore -= blueprint.ore_robot_ore_cost;
                self.ore_robots += 1;
            }
            RobotType::CLAY => {
                self.ore -= blueprint.clay_robot_ore_cost;
                self.clay_robots += 1;
            }
            RobotType::OBSIDIAN => {
                self.ore -= blueprint.obsidian_robot_ore_cost;
                self.clay -= blueprint.obsidian_robot_clay_cost;
                self.obsidian_robots += 1;
            }
            RobotType::GEODE => {
                self.ore -= blueprint.geode_robot_ore_cost;
                self.obsidian -= blueprint.geode_robot_obsidian_cost;
                self.geode_robots += 1;
            }
        }
    }
}
