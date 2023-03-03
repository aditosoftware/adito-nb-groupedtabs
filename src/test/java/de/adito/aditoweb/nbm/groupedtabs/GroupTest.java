package de.adito.aditoweb.nbm.groupedtabs;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.awt.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests methods inside the {@link Group} utillity class
 *
 * @author p.neub, 03.03.2023
 */
public class GroupTest
{
  /**
   * Checks that the {@link Group#colorForGroup(String)} function
   * always returns the same color for the same group
   *
   * @param pGroup the group
   */
  @ParameterizedTest
  @ValueSource(strings = {"MyEntity", "MyView", "MyContext", "SomeProcess", "Foo", "Bar"})
  void shouldAlwaysComputeTheSameColorForTheSameGroup(@NotNull String pGroup)
  {
    final Color color = Group.colorForGroup(pGroup);
    for (int i = 0; i < 100; i++)
    {
      final Color subsequent = Group.colorForGroup(pGroup);
      assertEquals(color, subsequent);
    }
  }

  /**
   * Checks that {@link Group#colorForGroup(String)} always returns a color from {@link Group#ALL}
   * and that the usage of colors is exactly one more than the number of colors,
   * if the number of groups exceedes the number of colors by 1
   */
  @Test
  void shouldRepeatColorIfMoreGroupsThanColorsExist()
  {
    final String[] groups = new String[Group.ALL.length + 1];
    for (int i = 0; i < groups.length; i++)
      groups[i] = "Foo" + i;

    final Map<Color, Integer> usages = new HashMap(Group.ALL.length);
    for (final Color color : Group.ALL)
      usages.put(color, 0);

    for (final String group : groups)
    {
      final Color color = Group.colorForGroup(group);

      // verify that only colors from Group.All are used
      assertTrue(usages.containsKey(color));

      usages.put(color, usages.get(color) + 1);
    }

    int totalUsages = usages.values().stream().mapToInt(Integer::intValue).sum();
    assertEquals(Group.ALL.length + 1, totalUsages);
  }
}
