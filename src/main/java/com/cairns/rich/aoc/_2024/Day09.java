package com.cairns.rich.aoc._2024;

import com.cairns.rich.aoc.Loader;
import com.google.common.collect.TreeMultimap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A compactly represented filesystem needs defragmenting.
 */
class Day09 extends Base2024 {
  /**
   * Files can be split apart block by block to make sure no space is wasted.
   *
   * By keeping a freelist that is sorted by addr, it is easy to find the leftmost free blocks.
   */
  @Override
  protected Object part1(Loader loader) {
    TreeMap<Long, Integer> freeAddrToWidth = new TreeMap<>();
    return computeChecksumAfterDefrag(
        loader,
        freeAddrToWidth::put,
        (newDiskRangeRegistrar, diskRange) -> {
          if (!freeAddrToWidth.isEmpty() && (freeAddrToWidth.firstKey() < diskRange.addr)) {
            long freeAddr = freeAddrToWidth.firstKey();
            int freeWidth = freeAddrToWidth.remove(freeAddr);
            if (freeWidth < diskRange.width) {
              newDiskRangeRegistrar.accept(new DiskRange(diskRange.fileId, freeAddr, freeWidth));
              diskRange.width -= freeWidth;
              return false;
            }
            diskRange.addr = freeAddr;
            if (freeWidth > diskRange.width) {
              freeAddrToWidth.put(freeAddr + diskRange.width, freeWidth - diskRange.width);
            }
          }
          return true;
        }
    );
  }

  /**
   * Because the file splitting is slow, files must be moved intact.
   *
   * By keeping a multiple freelists, grouped by sorted widths, and internally sorted by addr, it is efficient
   * to find the leftmost free space that can accommodate a file by using a tailset of the width keys and finding
   * the smallest address among their firstKey()s.  Any left over space can be returned to the free list.
   */
  @Override
  protected Object part2(Loader loader) {
    TreeMultimap<Integer, Long> widthToFreeAddr = TreeMultimap.create();
    Function<DiskRange, Optional<Integer>> widthOfLeftmostFreeAddrThatFits =
        (diskRange) -> widthToFreeAddr.keySet().tailSet(diskRange.width).stream()
            .sorted(Comparator.comparing((width) -> widthToFreeAddr.get(width).first()))
            .findFirst();
    return computeChecksumAfterDefrag(
        loader,
        (addr, width) -> widthToFreeAddr.put(width, addr),
        (newDiskRangeRegistrar, diskRange) -> {
          Optional<Integer> widthOpt = widthOfLeftmostFreeAddrThatFits.apply(diskRange);
          if (widthOpt.isPresent()) {
            int width = widthOpt.get();
            long freeAddr = widthToFreeAddr.get(width).first();
            if (freeAddr < diskRange.addr) {
              widthToFreeAddr.get(width).remove(freeAddr);
              diskRange.addr = freeAddr;
              if (width > diskRange.width) {
                widthToFreeAddr.put(width - diskRange.width, freeAddr + diskRange.width);
              }
            }
          }
          return true;
        }
    );
  }

  /**
   * Helper method to parse the given input, register all free blocks, defrag, and compute the checksum.
   * Note that the file {@link DiskRange}s are iterated backwards.
   * Defrags are allowed to add new ranges (which will not be re-defrag'd) using the given consumer.
   * Returning false from the defrag {@link BiPredicate} will result in the range being defrag'd again.
   */
  private long computeChecksumAfterDefrag(
      Loader loader,
      BiConsumer<Long, Integer> freeAddrWidthRegistrar,
      BiPredicate<Consumer<DiskRange>, DiskRange> defrag
  ) {
    List<DiskRange> diskRanges = parseAndRegisterFree(loader, freeAddrWidthRegistrar);
    for (int i = diskRanges.size() - 1; i >= 0; ) {
      if (defrag.test(diskRanges::add, diskRanges.get(i))) {
        --i;
      }
    }
    return diskRanges.stream().mapToLong(DiskRange::checksum).sum();
  }

  /**
   * Parses the given input and returns the file {@link DiskRange}s.
   * Will register each free range's addr and width with the given registrar .
   * The widths in the input string alternate between file and free starting with file.
   */
  private List<DiskRange> parseAndRegisterFree(Loader loader, BiConsumer<Long, Integer> freeAddrWidthRegistrar) {
    List<DiskRange> diskRanges = new ArrayList<>();
    String input = loader.sl();
    long addr = 0;
    for (int i = 0; i < input.length(); ++i) {
      int width = input.charAt(i) - '0';
      if (i % 2 == 0) {
        diskRanges.add(new DiskRange(i / 2, addr, width));
      }
      else {
        freeAddrWidthRegistrar.accept(addr, width);
      }
      addr += width;
    }
    return diskRanges;
  }

  /**
   * Container class describing a file disk range.
   */
  private static class DiskRange {
    private int fileId;
    private long addr;
    private int width;

    private DiskRange(int fileId, long addr, int width) {
      this.fileId = fileId;
      this.addr = addr;
      this.width = width;
    }

    /**
     * A checksum to verify our defrag.
     * This is an algebraically simplified version of the checksum algorithm:
     *   - sum of each file block's checksum
     *   - each block's checksum is its fileId * address (0 based)
     *   - note that the integer division in the below formula is dangerous and only works because
     *     exactly one of (width) or (width - 1) is guaranteed to be even (and safe for / 2).
     */
    private long checksum() {
      return (fileId > 0)
           ? fileId * (((width * (width - 1)) / 2) + (width * addr))
           : 0;
    }
  }
}
