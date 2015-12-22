/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Autogenerated by Thrift Compiler (0.9.2)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package backtype.storm.generated;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.annotation.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked"})
@Generated(value = "Autogenerated by Thrift Compiler (0.9.2)", date = "2015-12-22")
public class GetInfoOptions implements org.apache.thrift.TBase<GetInfoOptions, GetInfoOptions._Fields>, java.io.Serializable, Cloneable, Comparable<GetInfoOptions> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("GetInfoOptions");

  private static final org.apache.thrift.protocol.TField NUM_ERR_CHOICE_FIELD_DESC = new org.apache.thrift.protocol.TField("num_err_choice", org.apache.thrift.protocol.TType.I32, (short)1);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new GetInfoOptionsStandardSchemeFactory());
    schemes.put(TupleScheme.class, new GetInfoOptionsTupleSchemeFactory());
  }

  private NumErrorsChoice num_err_choice; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    /**
     * 
     * @see NumErrorsChoice
     */
    NUM_ERR_CHOICE((short)1, "num_err_choice");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // NUM_ERR_CHOICE
          return NUM_ERR_CHOICE;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final _Fields optionals[] = {_Fields.NUM_ERR_CHOICE};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.NUM_ERR_CHOICE, new org.apache.thrift.meta_data.FieldMetaData("num_err_choice", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, NumErrorsChoice.class)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(GetInfoOptions.class, metaDataMap);
  }

  public GetInfoOptions() {
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public GetInfoOptions(GetInfoOptions other) {
    if (other.is_set_num_err_choice()) {
      this.num_err_choice = other.num_err_choice;
    }
  }

  public GetInfoOptions deepCopy() {
    return new GetInfoOptions(this);
  }

  @Override
  public void clear() {
    this.num_err_choice = null;
  }

  /**
   * 
   * @see NumErrorsChoice
   */
  public NumErrorsChoice get_num_err_choice() {
    return this.num_err_choice;
  }

  /**
   * 
   * @see NumErrorsChoice
   */
  public void set_num_err_choice(NumErrorsChoice num_err_choice) {
    this.num_err_choice = num_err_choice;
  }

  public void unset_num_err_choice() {
    this.num_err_choice = null;
  }

  /** Returns true if field num_err_choice is set (has been assigned a value) and false otherwise */
  public boolean is_set_num_err_choice() {
    return this.num_err_choice != null;
  }

  public void set_num_err_choice_isSet(boolean value) {
    if (!value) {
      this.num_err_choice = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case NUM_ERR_CHOICE:
      if (value == null) {
        unset_num_err_choice();
      } else {
        set_num_err_choice((NumErrorsChoice)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case NUM_ERR_CHOICE:
      return get_num_err_choice();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case NUM_ERR_CHOICE:
      return is_set_num_err_choice();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof GetInfoOptions)
      return this.equals((GetInfoOptions)that);
    return false;
  }

  public boolean equals(GetInfoOptions that) {
    if (that == null)
      return false;

    boolean this_present_num_err_choice = true && this.is_set_num_err_choice();
    boolean that_present_num_err_choice = true && that.is_set_num_err_choice();
    if (this_present_num_err_choice || that_present_num_err_choice) {
      if (!(this_present_num_err_choice && that_present_num_err_choice))
        return false;
      if (!this.num_err_choice.equals(that.num_err_choice))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_num_err_choice = true && (is_set_num_err_choice());
    list.add(present_num_err_choice);
    if (present_num_err_choice)
      list.add(num_err_choice.getValue());

    return list.hashCode();
  }

  @Override
  public int compareTo(GetInfoOptions other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(is_set_num_err_choice()).compareTo(other.is_set_num_err_choice());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (is_set_num_err_choice()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.num_err_choice, other.num_err_choice);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("GetInfoOptions(");
    boolean first = true;

    if (is_set_num_err_choice()) {
      sb.append("num_err_choice:");
      if (this.num_err_choice == null) {
        sb.append("null");
      } else {
        sb.append(this.num_err_choice);
      }
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class GetInfoOptionsStandardSchemeFactory implements SchemeFactory {
    public GetInfoOptionsStandardScheme getScheme() {
      return new GetInfoOptionsStandardScheme();
    }
  }

  private static class GetInfoOptionsStandardScheme extends StandardScheme<GetInfoOptions> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, GetInfoOptions struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // NUM_ERR_CHOICE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.num_err_choice = backtype.storm.generated.NumErrorsChoice.findByValue(iprot.readI32());
              struct.set_num_err_choice_isSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, GetInfoOptions struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.num_err_choice != null) {
        if (struct.is_set_num_err_choice()) {
          oprot.writeFieldBegin(NUM_ERR_CHOICE_FIELD_DESC);
          oprot.writeI32(struct.num_err_choice.getValue());
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class GetInfoOptionsTupleSchemeFactory implements SchemeFactory {
    public GetInfoOptionsTupleScheme getScheme() {
      return new GetInfoOptionsTupleScheme();
    }
  }

  private static class GetInfoOptionsTupleScheme extends TupleScheme<GetInfoOptions> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, GetInfoOptions struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.is_set_num_err_choice()) {
        optionals.set(0);
      }
      oprot.writeBitSet(optionals, 1);
      if (struct.is_set_num_err_choice()) {
        oprot.writeI32(struct.num_err_choice.getValue());
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, GetInfoOptions struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(1);
      if (incoming.get(0)) {
        struct.num_err_choice = backtype.storm.generated.NumErrorsChoice.findByValue(iprot.readI32());
        struct.set_num_err_choice_isSet(true);
      }
    }
  }

}

