package tppe.deduplication;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({Case1Test.class, Case2Test.class, Case3Test.class, Case4Test.class, Case5Test.class, NameProcessorTest.class})
public class AllCasesSuite {
}