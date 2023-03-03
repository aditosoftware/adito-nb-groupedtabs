package de.adito.aditoweb.nbm.groupedtabs;

import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Contains helpers and definitions of groups
 *
 * @author p.neub, 28.02.2023
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Group
{
  public static final Object PROP_GROUP = new Object();

  public static final Color FALLBACK = Color.WHITE;

  public static final Color[] ALL = new Color[]{
      Color.decode("#A8071A"),
      Color.decode("#AD2102"),
      Color.decode("#AD4E00"),
      Color.decode("#AD6800"),
      Color.decode("#AD8B00"),
      Color.decode("#5B8C00"),
      Color.decode("#237804"),
      Color.decode("#006D75"),
      Color.decode("#0050B3"),
      Color.decode("#10239E"),
      Color.decode("#391085"),
      Color.decode("#9E1068"),
      Color.decode("#820014"),
      Color.decode("#871400"),
      Color.decode("#973800"),
      Color.decode("#874D00"),
      Color.decode("#876800"),
      Color.decode("#3F6600"),
      Color.decode("#135200"),
      Color.decode("#00474F"),
      Color.decode("#003A8C"),
      Color.decode("#061178"),
      Color.decode("#22075E"),
      Color.decode("#780650"),
      };

  /**
   * Computes the corrosponding color for the specified group.
   * This function has no side effects.
   *
   * @param pGroup the group
   * @return the color for the group
   */
  public static Color colorForGroup(@NotNull String pGroup)
  {
    // Math.abs will overflow and return a negative value if pHash is Integer.MIN_VALUE
    return Group.ALL[(pGroup.hashCode() & 0xfffffff) % Group.ALL.length];
  }
}
