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
@Generated(value = "Autogenerated by Thrift Compiler (0.9.2)", date = "2015-11-17")
public class SpoutAggregateStats implements org.apache.thrift.TBase<SpoutAggregateStats, SpoutAggregateStats._Fields>, java.io.Serializable, Cloneable, Comparable<SpoutAggregateStats> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("SpoutAggregateStats");

  private static final org.apache.thrift.protocol.TField COMPLETE_LATENCY_MS_FIELD_DESC = new org.apache.thrift.protocol.TField("complete_latency_ms", org.apache.thrift.protocol.TType.DOUBLE, (short)1);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new SpoutAggregateStatsStandardSchemeFactory());
    schemes.put(TupleScheme.class, new SpoutAggregateStatsTupleSchemeFactory());
  }

  private double complete_latency_ms; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    COMPLETE_LATENCY_MS((short)1, "complete_latency_ms");

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
        case 1: // COMPLETE_LATENCY_MS
          return COMPLETE_LATENCY_MS;
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
  private static final int __COMPLETE_LATENCY_MS_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  private static final _Fields optionals[] = {_Fields.COMPLETE_LATENCY_MS};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.COMPLETE_LATENCY_MS, new org.apache.thrift.meta_data.FieldMetaData("complete_latency_ms", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(SpoutAggregateStats.class, metaDataMap);
  }

  public SpoutAggregateStats() {
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public SpoutAggregateStats(SpoutAggregateStats other) {
    __isset_bitfield = other.__isset_bitfield;
    this.complete_latency_ms = other.complete_latency_ms;
  }

  public SpoutAggregateStats deepCopy() {
    return new SpoutAggregateStats(this);
  }

  @Override
  public void clear() {
    set_complete_latency_ms_isSet(false);
    this.complete_latency_ms = 0.0;
  }

  public double get_complete_latency_ms() {
    return this.complete_latency_ms;
  }

  public void set_complete_latency_ms(double complete_latency_ms) {
    this.complete_latency_ms = complete_latency_ms;
    set_complete_latency_ms_isSet(true);
  }

  public void unset_complete_latency_ms() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __COMPLETE_LATENCY_MS_ISSET_ID);
  }

  /** Returns true if field complete_latency_ms is set (has been assigned a value) and false otherwise */
  public boolean is_set_complete_latency_ms() {
    return EncodingUtils.testBit(__isset_bitfield, __COMPLETE_LATENCY_MS_ISSET_ID);
  }

  public void set_complete_latency_ms_isSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __COMPLETE_LATENCY_MS_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case COMPLETE_LATENCY_MS:
      if (value == null) {
        unset_complete_latency_ms();
      } else {
        set_complete_latency_ms((Double)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case COMPLETE_LATENCY_MS:
      return Double.valueOf(get_complete_latency_ms());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case COMPLETE_LATENCY_MS:
      return is_set_complete_latency_ms();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof SpoutAggregateStats)
      return this.equals((SpoutAggregateStats)that);
    return false;
  }

  public boolean equals(SpoutAggregateStats that) {
    if (that == null)
      return false;

    boolean this_present_complete_latency_ms = true && this.is_set_complete_latency_ms();
    boolean that_present_complete_latency_ms = true && that.is_set_complete_latency_ms();
    if (this_present_complete_latency_ms || that_present_complete_latency_ms) {
      if (!(this_present_complete_latency_ms && that_present_complete_latency_ms))
        return false;
      if (this.complete_latency_ms != that.complete_latency_ms)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_complete_latency_ms = true && (is_set_complete_latency_ms());
    list.add(present_complete_latency_ms);
    if (present_complete_latency_ms)
      list.add(complete_latency_ms);

    return list.hashCode();
  }

  @Override
  public int compareTo(SpoutAggregateStats other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(is_set_complete_latency_ms()).compareTo(other.is_set_complete_latency_ms());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (is_set_complete_latency_ms()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.complete_latency_ms, other.complete_latency_ms);
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
    StringBuilder sb = new StringBuilder("SpoutAggregateStats(");
    boolean first = true;

    if (is_set_complete_latency_ms()) {
      sb.append("complete_latency_ms:");
      sb.append(this.complete_latency_ms);
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
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class SpoutAggregateStatsStandardSchemeFactory implements SchemeFactory {
    public SpoutAggregateStatsStandardScheme getScheme() {
      return new SpoutAggregateStatsStandardScheme();
    }
  }

  private static class SpoutAggregateStatsStandardScheme extends StandardScheme<SpoutAggregateStats> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, SpoutAggregateStats struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // COMPLETE_LATENCY_MS
            if (schemeField.type == org.apache.thrift.protocol.TType.DOUBLE) {
              struct.complete_latency_ms = iprot.readDouble();
              struct.set_complete_latency_ms_isSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, SpoutAggregateStats struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.is_set_complete_latency_ms()) {
        oprot.writeFieldBegin(COMPLETE_LATENCY_MS_FIELD_DESC);
        oprot.writeDouble(struct.complete_latency_ms);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class SpoutAggregateStatsTupleSchemeFactory implements SchemeFactory {
    public SpoutAggregateStatsTupleScheme getScheme() {
      return new SpoutAggregateStatsTupleScheme();
    }
  }

  private static class SpoutAggregateStatsTupleScheme extends TupleScheme<SpoutAggregateStats> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, SpoutAggregateStats struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.is_set_complete_latency_ms()) {
        optionals.set(0);
      }
      oprot.writeBitSet(optionals, 1);
      if (struct.is_set_complete_latency_ms()) {
        oprot.writeDouble(struct.complete_latency_ms);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, SpoutAggregateStats struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(1);
      if (incoming.get(0)) {
        struct.complete_latency_ms = iprot.readDouble();
        struct.set_complete_latency_ms_isSet(true);
      }
    }
  }

}

