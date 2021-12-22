use std::io;
use std::io::Cursor;
use bitstream_io::{BigEndian, BitRead, BitReader, Endianness};

#[derive(PartialEq, PartialOrd, Eq, Ord, Debug, Hash)]
pub enum Packet {
    Literal { version: u8, id: u8, value: u128 },
    Operator { version: u8, id: u8, packets: Vec<Packet> },
}

impl Packet {
    pub fn parse(contents: &String) -> Packet {
        let data = hex::decode(contents.trim()).unwrap();
        return read_packet(&mut BitReader::endian(Cursor::new(data), BigEndian));
    }

    pub fn calculate(&self) -> u128 {
        return match self {
            Packet::Literal { version: _, id: _, value } => *value,
            Packet::Operator { version: _, id, packets } => {
                match id {
                    0 => packets.iter().map(|p| p.calculate()).sum(),
                    1 => packets.iter().map(|p| p.calculate()).product(),
                    2 => packets.iter().map(|p| p.calculate()).min().unwrap(),
                    3 => packets.iter().map(|p| p.calculate()).max().unwrap(),
                    5 => if &packets[0].calculate() > &packets[1].calculate() { 1 } else { 0 },
                    6 => if &packets[0].calculate() < &packets[1].calculate() { 1 } else { 0 },
                    7 => if &packets[0].calculate() == &packets[1].calculate() { 1 } else { 0 },
                    _ => todo!()
                }
            }
        };
    }

    pub fn sum_version(&self) -> u64 {
        return match self {
            Packet::Literal { version, id: _, value: _ } => *version as u64,
            Packet::Operator { version, id: _, packets } => {
                let mut total = *version as u64;
                for p in packets {
                    total += p.sum_version();
                }
                total
            }
        };
    }
}

fn read_packet<R: io::Read, E: Endianness>(reader: &mut BitReader<R, E>) -> Packet {
    fn recurse<R: io::Read, E: Endianness>(reader: &mut BitReader<R, E>) -> Option<(Packet, u64)> {
        let payload_version = reader.read::<u8>(3).unwrap();
        let mut total_consumed = 6;
        let packet = match reader.read::<u8>(3) {
            Ok(4) => {
                let mut literal = 0;
                while let Ok(section) = reader.read::<u128>(5) {
                    total_consumed += 5;
                    literal = literal << 4;
                    literal += section & 0b1111;
                    if section <= 0b1111 { break; }
                }
                Packet::Literal { version: payload_version, id: 4, value: literal }
            }
            Ok(id) => {
                let packets = match reader.read::<u8>(1) {
                    Ok(0) => {
                        // total length in bits
                        let length = reader.read::<u64>(15).unwrap();
                        total_consumed += 16;

                        let mut sub_consumed = 0;
                        let mut packets = vec!();
                        while let Some((packet, consumed)) = recurse(reader) {
                            total_consumed += consumed;
                            sub_consumed += consumed;
                            packets.push(packet);
                            if sub_consumed == length { break; }
                        }
                        packets
                    }
                    Ok(1) => {
                        // number of sub-packets immediately contained
                        let num_packets = reader.read::<u16>(11).unwrap();
                        total_consumed += 12;

                        let mut packets = vec!();
                        for _ in 0..num_packets {
                            let (packet, consumed) = recurse(reader).unwrap();
                            total_consumed += consumed;
                            packets.push(packet);
                        }
                        packets
                    }
                    _ => unreachable!(),
                };
                Packet::Operator { version: payload_version, id, packets }
            }
            Err(_) => return None
        };
        return Some((packet, total_consumed));
    }

    let (packet, _) = recurse(reader).unwrap();
    return packet;
}
