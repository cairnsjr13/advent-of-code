package com.cairns.rich.aoc._2019;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.cairns.rich.aoc._2019.IntCode.State;

class Day23 extends Base2019 {
  @Override
  protected void run() {
    List<Long> program = IntCode.parseProgram(fullLoader);
    ConcurrentLinkedDeque<Packet> packetsToSend = new ConcurrentLinkedDeque<>();
    Map<Long, Machine> machines = new HashMap<>();
    List<Computer> computers = IntStream.range(0, 50)
        .mapToObj((addr) -> new Computer(program, addr, packetsToSend))
        .collect(Collectors.toList());
    Nat nat = new Nat(computers, packetsToSend);
    computers.forEach((computer) -> machines.put(computer.addr, computer));
    machines.put(nat.addr, nat);
    
    while (!nat.shouldTerminate) {
      if (packetsToSend.isEmpty()) {
        Thread.yield();
      }
      else {
        Packet packet = packetsToSend.poll();
        machines.get(packet.dst).receive(packet);
      }
    }
  }
  
  private static abstract class Machine {
    protected final long addr;
    protected final ConcurrentLinkedDeque<Packet> packetsToReceive = new ConcurrentLinkedDeque<>();
    
    private Machine(long addr) {
      this.addr = addr;
    }
    
    protected abstract void receive(Packet packet);
  }
  
  private static class Nat extends Machine {
    private boolean hasRecv = false;
    private Packet lastRecv;
    private volatile boolean shouldTerminate = false;
    
    public Nat(List<Computer> computers, ConcurrentLinkedDeque<Packet> packetsToSend) {
      super(255);
      startDaemon(() -> restartLoop(computers, packetsToSend));
    }
    
    private void restartLoop(List<Computer> computers, ConcurrentLinkedDeque<Packet> packetsToSend) {
      Set<Long> ysSentToAddrZero = new HashSet<>();
      while (true) {
        boolean allNotSending = computers.stream().allMatch((c) -> c.notReceiving);
        boolean allNoneToRecvCountHigh = computers.stream().allMatch((c) -> c.noneToRecvCount >= 500);
        if ((lastRecv != null) && allNotSending && allNoneToRecvCountHigh) {
          packetsToSend.offer(new Packet(0L, lastRecv.x, lastRecv.y));
          if (!ysSentToAddrZero.add(lastRecv.y)) {
            shouldTerminate = true;
            System.out.println(lastRecv.y);
          }
          lastRecv = null;
        }
        Thread.yield();
      }
    }
    
    @Override
    protected void receive(Packet packet) {
      if (!hasRecv) {
        System.out.println(packet.y);
        hasRecv = true;
      }
      lastRecv = packet;
    }
  }
  
  private static class Computer extends Machine {
    private final State state;
    private volatile boolean notReceiving;
    private volatile int noneToRecvCount = 0;
    
    private Computer(List<Long> program, long addr, ConcurrentLinkedDeque<Packet> packetsToSend) {
      super(addr);
      this.state = IntCode.run(program);
      state.programInput.put(addr);
      startDaemon(() -> packetSenderLoop(packetsToSend));
      startDaemon(this::packetReceiverLoop);
    }
    
    private void packetSenderLoop(ConcurrentLinkedDeque<Packet> packetsToSend) {
      while (true) {
        state.blockUntilHaltOrOutput();
        long dst = state.programOutput.take();
        state.blockUntilHaltOrOutput();
        long x = state.programOutput.take();
        state.blockUntilHaltOrOutput();
        long y = state.programOutput.take();
        packetsToSend.add(new Packet(dst, x, y));
      }
    }
    
    private void packetReceiverLoop() {
      while (true) {
        notReceiving = true;
        state.blockUntilWaitForInput();
        notReceiving = false;
        if (packetsToReceive.isEmpty()) {
          state.programInput.put(-1);
          ++noneToRecvCount;
        }
        else {
          Packet packet = packetsToReceive.poll();
          state.programInput.put(packet.x);
          state.programInput.put(packet.y);
          noneToRecvCount = 0;
        }
      }
    }
    
    @Override
    protected void receive(Packet packet) {
      packetsToReceive.offer(packet);
    }
  }
  
  private static class Packet {
    private final long dst;
    private final long x;
    private final long y;
    
    private Packet(long dst, long x, long y) {
      this.dst = dst;
      this.x = x;
      this.y = y;
    }
  }
}
