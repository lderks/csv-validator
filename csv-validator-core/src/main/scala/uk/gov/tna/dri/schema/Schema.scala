/*
 * Copyright (c) 2013, The National Archives digitalpreservation@nationalarchives.gov.uk
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.tna.dri.schema

import uk.gov.tna.dri.metadata.Row
import util.parsing.input.Positional

case class Schema(globalDirectives: List[GlobalDirective], columnDefinitions: List[ColumnDefinition])

object Schema {
  val version = "1.0"
}

abstract class GlobalDirective(val name: String) extends Positional

case class Separator(separatorChar: Char) extends GlobalDirective("separator")

case class Quoted() extends GlobalDirective("quoted")

case class TotalColumns(numberOfColumns: Int) extends GlobalDirective("totalColumns")

case class NoHeader() extends GlobalDirective("noHeader")

case class IgnoreColumnNameCase() extends GlobalDirective("ignoreColumnNameCase")

case class ColumnDefinition(id: String, rules: List[Rule] = Nil, directives: List[ColumnDirective] = Nil) extends Positional

trait ArgProvider {

  def referenceValue(columnIndex: Int, row: Row, schema: Schema): Option[String]

  def toError: String
}

case class ColumnReference(value: String) extends ArgProvider {

  def referenceValue(columnIndex: Int, row: Row, schema: Schema): Option[String] = {
    val referencedIndex = schema.columnDefinitions.indexWhere(_.id == value)
    Some(row.cells(referencedIndex).value)
  }

  def toError ="$" + value
}

case class Literal(value: Option[String]) extends ArgProvider {

  def referenceValue(columnIndex: Int, row: Row, schema: Schema): Option[String] = value

  def toError = if (value.isDefined) "\"" + value.get + "\"" else ""
}

trait ColumnDirective extends Positional

case class Optional() extends ColumnDirective {
  override def toString = "optional"
}

case class Warning() extends ColumnDirective {
  override def toString = "warning"
}

case class IgnoreCase() extends ColumnDirective  {
  override def toString = "ignoreCase"
}