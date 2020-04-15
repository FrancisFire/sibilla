/**
 * generated by Xtext 2.17.0
 */
package quasylab.sibilla.lang.pm.tests;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import quasylab.sibilla.lang.pm.model.Constant;
import quasylab.sibilla.lang.pm.model.Model;
import quasylab.sibilla.lang.pm.tests.ModelInjectorProvider;

@ExtendWith(InjectionExtension.class)
@InjectWith(ModelInjectorProvider.class)
@SuppressWarnings("all")
public class ModelParsingTest {
  @Inject
  @Extension
  private ParseHelper<Model> _parseHelper;
  
  @Inject
  @Extension
  private ValidationTestHelper _validationTestHelper;
  
  @Test
  public void typeChecker() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("const A = 0.1;");
      _builder.newLine();
      _builder.append("const B = true;");
      _builder.newLine();
      _builder.append("const C = 10;");
      _builder.newLine();
      _builder.newLine();
      _builder.append("const D = A+C;");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.newLine();
      this._validationTestHelper.assertNoErrors(this._parseHelper.parse(_builder));
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public Constant getConstant(final Model m, final String name) {
    final Function1<Constant, Boolean> _function = (Constant it) -> {
      return Boolean.valueOf(it.getName().equals(name));
    };
    return IterableExtensions.<Constant>findFirst(Iterables.<Constant>filter(m.getElements(), Constant.class), _function);
  }
  
  @Test
  public void loadModel() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("species GA;\t\t\t");
      _builder.newLine();
      _builder.append("species CA;");
      _builder.newLine();
      _builder.append("species GB;");
      _builder.newLine();
      _builder.append("species CB;");
      _builder.newLine();
      _builder.newLine();
      _builder.append("macro meetrate = 1.0;");
      _builder.newLine();
      _builder.newLine();
      _builder.newLine();
      _builder.append("rule groupyA = GA -[ %CB*meetrate ]-> GB; ");
      _builder.newLine();
      _builder.append("rule groupyB = GB -[ %CA*meetrate ]-> GA;");
      _builder.newLine();
      _builder.append("rule celebrityA = CA -[ (%CA+%GA)*meetrate ]-> CB;");
      _builder.newLine();
      _builder.append("rule celebrityB = CB -[ (%CB+%GB)*meetrate ]-> CA;");
      _builder.newLine();
      _builder.newLine();
      this._validationTestHelper.assertNoErrors(this._parseHelper.parse(_builder));
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}