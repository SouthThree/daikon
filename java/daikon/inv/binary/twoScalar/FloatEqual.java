// ***** This file is automatically generated from IntComparisons.java.jpp

package daikon.inv.binary.twoScalar;

import daikon.*;
import daikon.inv.*;
import daikon.inv.unary.sequence.*;
import daikon.inv.unary.scalar.*;
import daikon.inv.binary.sequenceScalar.*;
import daikon.inv.binary.twoSequence.*;
import daikon.derive.*;
import daikon.derive.unary.*;

import utilMDE.*;

import org.apache.log4j.Category;

import java.util.*;

public final class FloatEqual
  extends TwoFloat  implements Comparison
{
  // We are Serializable, so we specify a version to allow changes to
  // method signatures without breaking serialization.  If you add or
  // remove fields, you should change this number to the current date.
  static final long serialVersionUID = 20020122L;

  // Variables starting with dkconfig_ should only be set via the
  // daikon.config.Configuration interface.
  /**
   * Boolean.  True iff FloatEqual  invariants should be considered.
   **/
  public static boolean dkconfig_enabled = true;

  public static final Category debug
    = Category.getInstance("daikon.inv.binary.twoScalar.FloatEqual");

  protected FloatEqual (PptSlice ppt) {
    super(ppt);
  }

  public static FloatEqual  instantiate(PptSlice ppt) {
    if (!dkconfig_enabled) return null;

    VarInfo var1 = ppt.var_infos[0];
    VarInfo var2 = ppt.var_infos[1];
    VarInfo seqvar1 = var1.isDerivedSequenceMember();
    VarInfo seqvar2 = var2.isDerivedSequenceMember();

    if (debug.isDebugEnabled() || ppt.debugged) {
      debug.debug("FloatEqual.instantiate(" + ppt.name + ")"
                          + ", seqvar1=" + seqvar1
                          + ", seqvar2=" + seqvar2);
    }

    { // Tests involving sequence lengths.

      SequenceLength sl1 = null;
      if (var1.isDerived() && (var1.derived instanceof SequenceLength))
        sl1 = (SequenceLength) var1.derived;
      SequenceLength sl2 = null;
      if (var2.isDerived() && (var2.derived instanceof SequenceLength))
        sl2 = (SequenceLength) var2.derived;

      // Avoid "size(a)-1 cmp size(b)-1"; use "size(a) cmp size(b)" instead.
      if ((sl1 != null) && (sl2 != null)
          && ((sl1.shift == sl2.shift) && (sl1.shift != 0) || (sl2.shift != 0))) {
        // "size(a)-1 cmp size(b)-1"; should just use "size(a) cmp size(b)"
        return null;
      }
    }

    return new FloatEqual (ppt);
  }

  protected Invariant resurrect_done_swapped() {

    // we don't care if things swap; we have symmetry
    return this;
  }

  // Look up a previously instantiated FloatEqual  relationship.
  // Should this implementation be made more efficient?
  public static FloatEqual  find(PptSlice ppt) {
    Assert.assertTrue(ppt.arity == 2);
    for (Iterator itor = ppt.invs.iterator(); itor.hasNext(); ) {
      Invariant inv = (Invariant) itor.next();
      if (inv instanceof FloatEqual)
        return (FloatEqual) inv;
    }
    return null;
  }

  public String repr() {
    return "FloatEqual"  + varNames();
  }

  public String format_using(OutputFormat format) {
    String var1name = var1().name.name_using(format);
    String var2name = var2().name.name_using(format);

    if ((format == OutputFormat.DAIKON)
        || (format == OutputFormat.ESCJAVA)
        || (format == OutputFormat.JML)
        || (format == OutputFormat.JAVA)
        || (format == OutputFormat.IOA))
    {
      String comparator = "==" ;

      if (format == OutputFormat.IOA) comparator = "=";

      return var1name + " " + comparator + " " + var2name;
    }

    if (format == OutputFormat.SIMPLIFY) {

    String comparator = "EQ";

      return "(" + comparator + " " + var1name + " " + var2name + ")";
    }

    return format_unimplemented(format);
  }

  public void add_modified(double  v1, double  v2, int count) {
    // if (ppt.debugged) {
    //   System.out.println("FloatEqual"  + ppt.varNames() + ".add_modified("
    //                      + v1 + "," + v2 + ", count=" + count + ")");
    // }
    if (!(v1 ==  v2)) {
      destroyAndFlow();
      return;
    }

  }

  // This is very tricky, because whether two variables are equal should
  // presumably be transitive, but it's not guaranteed to be so when using
  // this method and not dropping out all variables whose values are ever
  // missing.
  public double computeProbability() {
    if (falsified) {
      return Invariant.PROBABILITY_NEVER;
    }
    // Should perhaps check number of samples and be unjustified if too few
    // samples.

    // We MUST check if we have seen samples; otherwise we get
    // undesired transitivity with missing values.
    if (ppt.num_samples() == 0) {
      return Invariant.PROBABILITY_UNJUSTIFIED;
    }

    // It's an equality invariant.  I ought to use the actual ranges somehow.
    // Actually, I can't even use this .5 test because it can make
    // equality non-transitive.
    // return Math.pow(.5, num_values());
    return Invariant.PROBABILITY_JUSTIFIED;
  }

  public boolean enoughSamples() {
    return (ppt.num_samples() > 0);
  }

  // For Comparison interface
  public double eq_probability() {
    if (isExact())
      return computeProbability();
    else
      return Invariant.PROBABILITY_NEVER;
  }

  public boolean isExact() {

    return true;
  }

  // // Temporary, for debugging
  // public void destroy() {
  //   if (debug.isDebugEnabled() || ppt.debugged) {
  //     System.out.println("FloatEqual.destroy(" + ppt.name + ")");
  //     System.out.println(repr());
  //     (new Error()).printStackTrace();
  //   }
  //   super.destroy();
  // }

  public void add(double  v1, double  v2, int mod_index, int count) {
    if (ppt.debugged) {
      System.out.println("FloatEqual"  + ppt.varNames() + ".add("
                         + v1 + "," + v2
                         + ", mod_index=" + mod_index + ")"
                         + ", count=" + count + ")");
    }
    super.add(v1, v2, mod_index, count);
  }

  public boolean isSameFormula(Invariant other)
  {
    return true;
  }

  public boolean isExclusiveFormula(Invariant other)
  {
    // Also ought to check against LinearBinary, etc.

    if ((other instanceof FloatLessThan  ) || (other instanceof FloatGreaterThan  ))
      return true;

    return false;
  }

  public boolean isObviousImplied() {
    VarInfo var1 = ppt.var_infos[0];
    VarInfo var2 = ppt.var_infos[1];

    // a+c=b+c is implied, because a=b must have also been reported.
    return ((var1.name instanceof VarInfoName.Add) && (var2.name instanceof VarInfoName.Add) &&
              ((((VarInfoName.Add) var1.name).amount) == (((VarInfoName.Add) var2.name).amount)));

  } // isObviousImplied

  /* [INCR]
  public boolean hasNonCanonicalVariable() {
    VarInfo[] vis = ppt.var_infos;
    return ! (vis[0].isCanonical() || vis[1].isCanonical());
  }
  */ // ... [INCR]

}
