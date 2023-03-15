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
      new Color(0xF5222D),
      new Color(0xFA541C),
      new Color(0xFA8C16),
      new Color(0xFAAD14),
      new Color(0xFADB14),
      new Color(0xA0D911),
      new Color(0x52C41A),
      new Color(0x13C2C2),
      new Color(0x1890FF),
      new Color(0x2F54EB),
      new Color(0x722ED1),
      new Color(0xEB2F96),
      new Color(0xCF1322),
      new Color(0xD4380D),
      new Color(0xD46B08),
      new Color(0xD48806),
      new Color(0xD4B106),
      new Color(0x7CB305),
      new Color(0x389E0D),
      new Color(0x08979C),
      new Color(0x096DD9),
      new Color(0x1D39C4),
      new Color(0x531DAB),
      new Color(0xC41D7F),
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
