package test;

import junit.framework.Test;
import junit.framework.TestSuite;
import android.test.suitebuilder.TestSuiteBuilder;

public class TestAll extends TestSuite
{
	public static Test suite()
	{
		return new TestSuiteBuilder(TestAll.class).includePackages(
				"test.MyAndroidTestCase").build();
	}
}
