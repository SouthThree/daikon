package daikon.diff;

import java.io.*;
import java.text.*;
import daikon.inv.*;
import daikon.inv.unary.*;
import daikon.inv.unary.scalar.*;
import utilMDE.*;

public class DetailedStatisticsVisitor implements NodeVisitor {

  private static final int FIELD_WIDTH = 5;
  private static final int LABEL_WIDTH = 7;

  // Types of invariants
  public static final int NUM_TYPES = 6;
  public static final int TYPE_NULLARY_INTERESTING = 0;
  public static final int TYPE_NULLARY_UNINTERESTING = 1;
  public static final int TYPE_UNARY_INTERESTING = 2;
  public static final int TYPE_UNARY_UNINTERESTING = 3;
  public static final int TYPE_BINARY = 4;
  public static final int TYPE_TERNARY = 5;

  public static final String[] TYPE_LABELS =
  { "NInt", "N!Int", "UInt", "U!Int", "Bin", "Ter" };

  // Relationships between invariants
  public static final int NUM_RELATIONSHIPS = 12;
  // Both present, same invariant, justification in file1, justified in file2
  public static final int REL_SAME_JUST1_JUST2 = 0;
  // Both present, same invariant, justified in file1, unjustified in file2
  public static final int REL_SAME_JUST1_UNJUST2 = 1;
  // Both present, same invariant, unjustified in file1, justified in file2
  public static final int REL_SAME_UNJUST1_JUST2 = 2;
  // Both present, same invariant, unjustified in file1, unjustified in file2
  public static final int REL_SAME_UNJUST1_UNJUST2 = 3;
  // Both present, diff invariant, justification in file1, justified
  // in file2
  public static final int REL_DIFF_JUST1_JUST2 = 4;
  // Both present, different invariant, justified in file1,
  // unjustified in file2
  public static final int REL_DIFF_JUST1_UNJUST2 = 5;
  // Both present, different invariant, unjustified in file1,
  // justified in file2
  public static final int REL_DIFF_UNJUST1_JUST2 = 6;
  // Both present, different invariant, unjustified in file1,
  // unjustified in file2
  public static final int REL_DIFF_UNJUST1_UNJUST2 = 7;
  // Present in file1, justified in file1, not present in file2
  public static final int REL_MISS_JUST1 = 8;
  // Present in file1, unjustified in file1, not present in file2
  public static final int REL_MISS_UNJUST1 = 9;
  // Not present in file1, present in file2, justified in file2
  public static final int REL_MISS_JUST2 = 10;
  // Not present in file1, present in file2, unjustified in file2
  public static final int REL_MISS_UNJUST2 = 11;

  // Table of frequencies, indexed by type of invariant, and
  // relationship between the invariants
  private double[][] freq = new double[NUM_TYPES][NUM_RELATIONSHIPS];

  private boolean continuousJustification;

  public DetailedStatisticsVisitor(boolean continuousJustification) {
    this.continuousJustification = continuousJustification;
  }

  public void visitRootNode(RootNode node) { }

  public void visitPptNode(PptNode node) { }

  public void visitInvNode(InvNode node) {
    addFrequency(node.getInv1(), node.getInv2());
  }

  public void addFrequency(Invariant inv1, Invariant inv2) {
    if (continuousJustification) {
      addFrequencyContinuous(inv1, inv2);
    } else {
      addFrequencyBinary(inv1, inv2);
    }
  }

  public void addFrequencyBinary(Invariant inv1, Invariant inv2) {
    int type = determineType(inv1, inv2);
    int relationship = determineRelationship(inv1, inv2);
    freq[type][relationship]++;
  }

  public void addFrequencyContinuous(Invariant inv1, Invariant inv2) {
    int type = determineType(inv1, inv2);
    int relationship = determineRelationship(inv1, inv2);

    switch (relationship) {
    case REL_SAME_JUST1_UNJUST2: case REL_SAME_UNJUST1_JUST2:
      freq[type][relationship] += calculateProbabilityDifference(inv1, inv2);
      break;
    default:
      freq[type][relationship]++;      
    }
    
  }

  public static double calculateProbabilityDifference(Invariant inv1,
                                                      Invariant inv2) {
    Assert.assert(inv1 != null && inv2 != null);
    double prob1 = Math.min(inv1.getProbability(), 1);
    double prob2 = Math.min(inv2.getProbability(), 1);
    double diff = Math.abs(prob1 - prob2);
    return diff;
  }

  public static int determineType(Invariant inv1, Invariant inv2) {
    int type;

    // Set inv to a non-null invariant
    Invariant inv = (inv1 != null) ? inv1 : inv2;

    // If either invariant is interesting, the pair is interesting
    boolean interesting = ((inv1 != null && inv1.isInteresting()) ||
                           (inv2 != null && inv2.isInteresting()));

    int arity = inv.ppt.arity;
    switch (arity) {
    case 0:
      type = interesting ? TYPE_NULLARY_INTERESTING :
        TYPE_NULLARY_UNINTERESTING;
      break;
    case 1:
      type = interesting ? TYPE_UNARY_INTERESTING : TYPE_UNARY_UNINTERESTING;
      break;
    case 2:
      type = TYPE_BINARY;
      break;
    case 3:
      type = TYPE_TERNARY;
      break;
    default:
      throw new Error("Invalid arity: " + arity);
    }

    return type;
  }

  public static int determineRelationship(Invariant inv1, Invariant inv2) {
    int relationship;

    if (inv1 == null) {
      relationship = inv2.justified() ? REL_MISS_JUST2 : REL_MISS_UNJUST2;
    } else if (inv2 == null) {
      relationship = inv1.justified() ? REL_MISS_JUST1 : REL_MISS_UNJUST1;
    } else {
      boolean justified1 = inv1.justified();
      boolean justified2 = inv2.justified();
      if (inv1.isSameInvariant(inv2)) {
        if (justified1 && justified2) {
          relationship = REL_SAME_JUST1_JUST2;
        } else if (justified1 && !justified2) {
          relationship = REL_SAME_JUST1_UNJUST2;
        } else if (!justified1 && justified2) {
          relationship = REL_SAME_UNJUST1_JUST2;
        } else {
          relationship = REL_SAME_UNJUST1_UNJUST2;
        }
      } else {
        if (justified1 && justified2) {
          relationship = REL_DIFF_JUST1_JUST2;
        } else if (justified1 && !justified2) {
          relationship = REL_DIFF_JUST1_UNJUST2;
        } else if (!justified1 && justified2) {
          relationship = REL_DIFF_UNJUST1_JUST2;
        } else {
          relationship = REL_DIFF_UNJUST1_UNJUST2;
        }
      }
    }

    return relationship;
  }

  // Returns a tab-separated listing of its data, suitable for storing in a
  // file
  public String repr() {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    
    for (int type=0; type < NUM_TYPES; type++) {
      for (int rel=0; rel < NUM_RELATIONSHIPS; rel++) {
        pw.println(String.valueOf(type) + "\t" + String.valueOf(rel) + "\t" +
                   String.valueOf(freq[type][rel]));
      }
    }

    return sw.toString();
  }

  // Returns a human-readable table of its data
  public String format() {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    
    pw.println("STATISTICS");
    pw.print("       ");
    for (int rel = 0; rel < NUM_RELATIONSHIPS; rel++) {
      pw.print(UtilMDE.rpad(rel, FIELD_WIDTH));
    }
    pw.println(UtilMDE.rpad("TOTAL", FIELD_WIDTH));

    for (int type = 0; type < NUM_TYPES; type++) {
      pw.print(UtilMDE.rpad(TYPE_LABELS[type], LABEL_WIDTH));
      for (int rel = 0; rel < NUM_RELATIONSHIPS; rel++) {
        int f = (int) freq[type][rel];
        pw.print(UtilMDE.rpad(f, FIELD_WIDTH));
      }
      int s = (int) ArraysMDE.sum(freq[type]);
      pw.print(UtilMDE.rpad(s, FIELD_WIDTH));
      pw.println();
    }

    pw.print(UtilMDE.rpad("TOTAL", LABEL_WIDTH));
    for (int rel = 0; rel < NUM_RELATIONSHIPS; rel++) {
      int sum = 0;
      for (int type = 0; type < NUM_TYPES; type++) {
        sum += freq[type][rel];
      }
      pw.print(UtilMDE.rpad(sum, FIELD_WIDTH));
    }
    pw.print(UtilMDE.rpad((int) ArraysMDE.sum(freq), FIELD_WIDTH));

    pw.println();

    pw.println();

    return sw.toString();
  }

  // Use this method instead of making the array public, to preserve
  // abstraction
  public double freq(int type, int relationship) {
    return freq[type][relationship];
  }

}
