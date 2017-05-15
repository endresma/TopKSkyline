package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
   EBNLTopKTest.class,
   ESFSTopKTest.class,
   SequentialTopKTest.class,
   ParallelTopKTest.class,
   TopSortTest.class
})

public class TestSuite {   
}  