package tppe.deduplication;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({Case3Test.class, Case4Test.class, Case5Test.class})
public class AllCasesSuite {
}
