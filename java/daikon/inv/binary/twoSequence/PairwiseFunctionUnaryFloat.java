// ***** This file is automatically generated from PairwiseFunctionUnary.java.jpp

package daikon.inv.binary.twoSequence;

import daikon.*;
import daikon.inv.Invariant;
import daikon.inv.binary.twoScalar.*;
import java.lang.reflect.*;

/**
 * That each element from one sequence relates to each corresponding
 * element in another sequence by a function.
 **/
public class PairwiseFunctionUnaryFloat
  extends TwoSequenceFloat
{
  // We are Serializable, so we specify a version to allow changes to
  // method signatures without breaking serialization.  If you add or
  // remove fields, you should change this number to the current date.
  static final long serialVersionUID = 20020122L;

  // Variables starting with dkconfig_ should only be set via the
  // daikon.config.Configuration interface.
  /**
   * Boolean.  True iff PairwiseFunctionUnary invariants should be considered.
   **/
  public static boolean dkconfig_enabled = true;

  public FunctionUnaryCoreFloat  core;

  protected PairwiseFunctionUnaryFloat (PptSlice ppt, String methodname, int function, boolean inverse) {
    super(ppt);
    core = new FunctionUnaryCoreFloat (this, methodname, function, inverse);
  }

  public static PairwiseFunctionUnaryFloat  instantiate(PptSlice ppt, String methodname, int methodnumber, boolean inverse) {
    if (!dkconfig_enabled) return null;
     PairwiseFunctionUnaryFloat  result =
      new PairwiseFunctionUnaryFloat (ppt, methodname, methodnumber, inverse);
    // Don't instantiate if the variables can't have order
    if (!result.var1().aux.getFlag(VarInfoAux.HAS_ORDER) ||
        !result.var2().aux.getFlag(VarInfoAux.HAS_ORDER)) {
      if (debug.isDebugEnabled()) {
        debug.debug ("Not instantitating for because order has no meaning: " +
                     result.var1().name + " and " + result.var2().name);
      }
      return null;
    }
    return result;
  }

  protected Object clone() {
    PairwiseFunctionUnaryFloat  result = (PairwiseFunctionUnaryFloat) super.clone();
    result.core = (FunctionUnaryCoreFloat) core.clone();
    result.core.wrapper = result;
    return result;
  }

  protected Invariant resurrect_done_swapped() {
    core.swap();
    return this;
  }

  public String repr() {
    return "PairwiseFunctionUnaryFloat"  + varNames() + ": " + core.repr();
  }

  public String format_using(OutputFormat format) {
    if (format == OutputFormat.IOA) {
      return format_ioa();
    }

    return core.format_using(format, var1().name, var2().name);
  }

  /* IOA */
  public String format_ioa() {
    if (var1().isIOASet() || var2().isIOASet())
      return "Not valid for sets: " + format();
    VarInfoName.QuantHelper.IOAQuantification quant1 = new VarInfoName.QuantHelper.IOAQuantification(var1());
    VarInfoName.QuantHelper.IOAQuantification quant2 = new VarInfoName.QuantHelper.IOAQuantification(var2());

    return quant1.getQuantifierExp()
      + core.format_using(OutputFormat.IOA,
                          quant1.getVarName(0),
                          quant2.getVarName(0))
      + quant1.getClosingExp();
  }

  public void add_modified(double [] x_arr, double [] y_arr, int count) {
    if (x_arr.length != y_arr.length) {
      destroyAndFlow();
      return;
    }
    int len = x_arr.length;
    // int len = Math.min(x_arr.length, y_arr.length);

    for (int i=0; i<len; i++) {
      double  x  = x_arr[i];
      double  y = y_arr[i];

      core.add_modified(x, y, count);
      if (falsified)
        return;
    }
  }

  protected double computeProbability() {
    return core.computeProbability();
  }

  public boolean isSameFormula(Invariant other)
  {
    return core.isSameFormula(((PairwiseFunctionUnaryFloat) other).core);
  }

}
