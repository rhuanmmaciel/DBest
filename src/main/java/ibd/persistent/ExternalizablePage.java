/*
 * This file is adapted from ELKI:
 * Environment for Developing KDD-Applications Supported by Index-Structures
 *
 * Copyright (C) 2022
 * ELKI Development Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ibd.persistent;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
/**
 * Base interface for externalizable pages.
 * 
 * @author Elke Achtert - Adapted by Sergio Mergen
 * @since 0.5.0
 */
// TODO: replace with the newer ByteSerializers
public interface ExternalizablePage  {
  // Empty
    void readExternal(DataInput out) throws IOException;
    void writeExternal(DataOutput out) throws IOException;
    int getSizeInBytes();
}
