package io.cdap.directives.aggregates;

import io.cdap.cdap.etl.api.Lookup;
import io.cdap.cdap.etl.api.StageMetrics;
import io.cdap.wrangler.TestingRig;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.TransientStore;
import io.cdap.wrangler.api.TransientVariableScope;
import io.cdap.wrangler.proto.Contexts;
import org.junit.Assert;
import org.junit.Test;

import java.net.URL;
import java.util.*;

/**
 * Tests {@link Aggregate}
 */
public class AggregateTest {

  @Test
  public void testSumAggregation() throws Exception {
    String[] recipe = new String[] {
      "aggregate class score sum"
    };

    List<Row> rows = new ArrayList<>();
    rows.add(new Row("class", "A").add("score", 10));
    rows.add(new Row("class", "A").add("score", 15));
    rows.add(new Row("class", "B").add("score", 5));

    final Map<String, Object> transientVars = new HashMap<>();
    rows = TestingRig.execute(recipe, rows, new ExecutorContext() {
      @Override
      public Environment getEnvironment() {
        return Environment.TESTING;
      }

      @Override
      public String getNamespace() {
        return Contexts.SYSTEM;
      }

      @Override
      public StageMetrics getMetrics() {
        return null;
      }

      @Override
      public String getContextName() {
        return "aggregate-test";
      }

      @Override
      public Map<String, String> getProperties() {
        return new HashMap<>();
      }

      @Override
      public URL getService(String applicationId, String serviceId) {
        return null;
      }

      @Override
      public TransientStore getTransientStore() {
        return new TransientStore() {
          @Override
          public void reset(TransientVariableScope scope) { }

          @Override
          public <T> T get(String name) {
            return (T) transientVars.get(name);
          }

          @Override
          public void set(TransientVariableScope scope, String name, Object value) {
            transientVars.put(name, value);
          }

          @Override
          public void increment(TransientVariableScope scope, String name, long value) { }

          @Override
          public Set<String> getVariables() {
            return transientVars.keySet();
          }
        };
      }

      @Override
      public <T> Lookup<T> provide(String s, Map<String, String> map) {
        return null;
      }
    });

    // Verifying output
    Map<String, Integer> expectedResults = new HashMap<>();
    expectedResults.put("A", 25);
    expectedResults.put("B", 5);

    for (Row row : rows) {
      String group = (String) row.getValue("class");
      int score = (Integer) row.getValue("score");
      Assert.assertEquals(expectedResults.get(group).intValue(), score);
    }
  }
}
