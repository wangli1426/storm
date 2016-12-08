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
 * Autogenerated by Thrift Compiler (0.9.3)
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
@Generated(value = "Autogenerated by Thrift Compiler (0.9.3)", date = "2016-12-07")
public class ExecutorStats implements org.apache.thrift.TBase<ExecutorStats, ExecutorStats._Fields>, java.io.Serializable, Cloneable, Comparable<ExecutorStats> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("ExecutorStats");

  private static final org.apache.thrift.protocol.TField EMITTED_FIELD_DESC = new org.apache.thrift.protocol.TField("emitted", org.apache.thrift.protocol.TType.MAP, (short)1);
  private static final org.apache.thrift.protocol.TField TRANSFERRED_FIELD_DESC = new org.apache.thrift.protocol.TField("transferred", org.apache.thrift.protocol.TType.MAP, (short)2);
  private static final org.apache.thrift.protocol.TField SPECIFIC_FIELD_DESC = new org.apache.thrift.protocol.TField("specific", org.apache.thrift.protocol.TType.STRUCT, (short)3);
  private static final org.apache.thrift.protocol.TField RATE_FIELD_DESC = new org.apache.thrift.protocol.TField("rate", org.apache.thrift.protocol.TType.DOUBLE, (short)4);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new ExecutorStatsStandardSchemeFactory());
    schemes.put(TupleScheme.class, new ExecutorStatsTupleSchemeFactory());
  }

  private Map<String,Map<String,Long>> emitted; // required
  private Map<String,Map<String,Long>> transferred; // required
  private ExecutorSpecificStats specific; // required
  private double rate; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    EMITTED((short)1, "emitted"),
    TRANSFERRED((short)2, "transferred"),
    SPECIFIC((short)3, "specific"),
    RATE((short)4, "rate");

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
        case 1: // EMITTED
          return EMITTED;
        case 2: // TRANSFERRED
          return TRANSFERRED;
        case 3: // SPECIFIC
          return SPECIFIC;
        case 4: // RATE
          return RATE;
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
  private static final int __RATE_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.EMITTED, new org.apache.thrift.meta_data.FieldMetaData("emitted", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.MapMetaData(org.apache.thrift.protocol.TType.MAP, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING), 
            new org.apache.thrift.meta_data.MapMetaData(org.apache.thrift.protocol.TType.MAP, 
                new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING), 
                new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)))));
    tmpMap.put(_Fields.TRANSFERRED, new org.apache.thrift.meta_data.FieldMetaData("transferred", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.MapMetaData(org.apache.thrift.protocol.TType.MAP, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING), 
            new org.apache.thrift.meta_data.MapMetaData(org.apache.thrift.protocol.TType.MAP, 
                new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING), 
                new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)))));
    tmpMap.put(_Fields.SPECIFIC, new org.apache.thrift.meta_data.FieldMetaData("specific", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, ExecutorSpecificStats.class)));
    tmpMap.put(_Fields.RATE, new org.apache.thrift.meta_data.FieldMetaData("rate", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(ExecutorStats.class, metaDataMap);
  }

  public ExecutorStats() {
  }

  public ExecutorStats(
    Map<String,Map<String,Long>> emitted,
    Map<String,Map<String,Long>> transferred,
    ExecutorSpecificStats specific,
    double rate)
  {
    this();
    this.emitted = emitted;
    this.transferred = transferred;
    this.specific = specific;
    this.rate = rate;
    set_rate_isSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public ExecutorStats(ExecutorStats other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.is_set_emitted()) {
      Map<String,Map<String,Long>> __this__emitted = new HashMap<String,Map<String,Long>>(other.emitted.size());
      for (Map.Entry<String, Map<String,Long>> other_element : other.emitted.entrySet()) {

        String other_element_key = other_element.getKey();
        Map<String,Long> other_element_value = other_element.getValue();

        String __this__emitted_copy_key = other_element_key;

        Map<String,Long> __this__emitted_copy_value = new HashMap<String,Long>(other_element_value);

        __this__emitted.put(__this__emitted_copy_key, __this__emitted_copy_value);
      }
      this.emitted = __this__emitted;
    }
    if (other.is_set_transferred()) {
      Map<String,Map<String,Long>> __this__transferred = new HashMap<String,Map<String,Long>>(other.transferred.size());
      for (Map.Entry<String, Map<String,Long>> other_element : other.transferred.entrySet()) {

        String other_element_key = other_element.getKey();
        Map<String,Long> other_element_value = other_element.getValue();

        String __this__transferred_copy_key = other_element_key;

        Map<String,Long> __this__transferred_copy_value = new HashMap<String,Long>(other_element_value);

        __this__transferred.put(__this__transferred_copy_key, __this__transferred_copy_value);
      }
      this.transferred = __this__transferred;
    }
    if (other.is_set_specific()) {
      this.specific = new ExecutorSpecificStats(other.specific);
    }
    this.rate = other.rate;
  }

  public ExecutorStats deepCopy() {
    return new ExecutorStats(this);
  }

  @Override
  public void clear() {
    this.emitted = null;
    this.transferred = null;
    this.specific = null;
    set_rate_isSet(false);
    this.rate = 0.0;
  }

  public int get_emitted_size() {
    return (this.emitted == null) ? 0 : this.emitted.size();
  }

  public void put_to_emitted(String key, Map<String,Long> val) {
    if (this.emitted == null) {
      this.emitted = new HashMap<String,Map<String,Long>>();
    }
    this.emitted.put(key, val);
  }

  public Map<String,Map<String,Long>> get_emitted() {
    return this.emitted;
  }

  public void set_emitted(Map<String,Map<String,Long>> emitted) {
    this.emitted = emitted;
  }

  public void unset_emitted() {
    this.emitted = null;
  }

  /** Returns true if field emitted is set (has been assigned a value) and false otherwise */
  public boolean is_set_emitted() {
    return this.emitted != null;
  }

  public void set_emitted_isSet(boolean value) {
    if (!value) {
      this.emitted = null;
    }
  }

  public int get_transferred_size() {
    return (this.transferred == null) ? 0 : this.transferred.size();
  }

  public void put_to_transferred(String key, Map<String,Long> val) {
    if (this.transferred == null) {
      this.transferred = new HashMap<String,Map<String,Long>>();
    }
    this.transferred.put(key, val);
  }

  public Map<String,Map<String,Long>> get_transferred() {
    return this.transferred;
  }

  public void set_transferred(Map<String,Map<String,Long>> transferred) {
    this.transferred = transferred;
  }

  public void unset_transferred() {
    this.transferred = null;
  }

  /** Returns true if field transferred is set (has been assigned a value) and false otherwise */
  public boolean is_set_transferred() {
    return this.transferred != null;
  }

  public void set_transferred_isSet(boolean value) {
    if (!value) {
      this.transferred = null;
    }
  }

  public ExecutorSpecificStats get_specific() {
    return this.specific;
  }

  public void set_specific(ExecutorSpecificStats specific) {
    this.specific = specific;
  }

  public void unset_specific() {
    this.specific = null;
  }

  /** Returns true if field specific is set (has been assigned a value) and false otherwise */
  public boolean is_set_specific() {
    return this.specific != null;
  }

  public void set_specific_isSet(boolean value) {
    if (!value) {
      this.specific = null;
    }
  }

  public double get_rate() {
    return this.rate;
  }

  public void set_rate(double rate) {
    this.rate = rate;
    set_rate_isSet(true);
  }

  public void unset_rate() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __RATE_ISSET_ID);
  }

  /** Returns true if field rate is set (has been assigned a value) and false otherwise */
  public boolean is_set_rate() {
    return EncodingUtils.testBit(__isset_bitfield, __RATE_ISSET_ID);
  }

  public void set_rate_isSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __RATE_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case EMITTED:
      if (value == null) {
        unset_emitted();
      } else {
        set_emitted((Map<String,Map<String,Long>>)value);
      }
      break;

    case TRANSFERRED:
      if (value == null) {
        unset_transferred();
      } else {
        set_transferred((Map<String,Map<String,Long>>)value);
      }
      break;

    case SPECIFIC:
      if (value == null) {
        unset_specific();
      } else {
        set_specific((ExecutorSpecificStats)value);
      }
      break;

    case RATE:
      if (value == null) {
        unset_rate();
      } else {
        set_rate((Double)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case EMITTED:
      return get_emitted();

    case TRANSFERRED:
      return get_transferred();

    case SPECIFIC:
      return get_specific();

    case RATE:
      return get_rate();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case EMITTED:
      return is_set_emitted();
    case TRANSFERRED:
      return is_set_transferred();
    case SPECIFIC:
      return is_set_specific();
    case RATE:
      return is_set_rate();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof ExecutorStats)
      return this.equals((ExecutorStats)that);
    return false;
  }

  public boolean equals(ExecutorStats that) {
    if (that == null)
      return false;

    boolean this_present_emitted = true && this.is_set_emitted();
    boolean that_present_emitted = true && that.is_set_emitted();
    if (this_present_emitted || that_present_emitted) {
      if (!(this_present_emitted && that_present_emitted))
        return false;
      if (!this.emitted.equals(that.emitted))
        return false;
    }

    boolean this_present_transferred = true && this.is_set_transferred();
    boolean that_present_transferred = true && that.is_set_transferred();
    if (this_present_transferred || that_present_transferred) {
      if (!(this_present_transferred && that_present_transferred))
        return false;
      if (!this.transferred.equals(that.transferred))
        return false;
    }

    boolean this_present_specific = true && this.is_set_specific();
    boolean that_present_specific = true && that.is_set_specific();
    if (this_present_specific || that_present_specific) {
      if (!(this_present_specific && that_present_specific))
        return false;
      if (!this.specific.equals(that.specific))
        return false;
    }

    boolean this_present_rate = true;
    boolean that_present_rate = true;
    if (this_present_rate || that_present_rate) {
      if (!(this_present_rate && that_present_rate))
        return false;
      if (this.rate != that.rate)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_emitted = true && (is_set_emitted());
    list.add(present_emitted);
    if (present_emitted)
      list.add(emitted);

    boolean present_transferred = true && (is_set_transferred());
    list.add(present_transferred);
    if (present_transferred)
      list.add(transferred);

    boolean present_specific = true && (is_set_specific());
    list.add(present_specific);
    if (present_specific)
      list.add(specific);

    boolean present_rate = true;
    list.add(present_rate);
    if (present_rate)
      list.add(rate);

    return list.hashCode();
  }

  @Override
  public int compareTo(ExecutorStats other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(is_set_emitted()).compareTo(other.is_set_emitted());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (is_set_emitted()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.emitted, other.emitted);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(is_set_transferred()).compareTo(other.is_set_transferred());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (is_set_transferred()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.transferred, other.transferred);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(is_set_specific()).compareTo(other.is_set_specific());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (is_set_specific()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.specific, other.specific);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(is_set_rate()).compareTo(other.is_set_rate());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (is_set_rate()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.rate, other.rate);
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
    StringBuilder sb = new StringBuilder("ExecutorStats(");
    boolean first = true;

    sb.append("emitted:");
    if (this.emitted == null) {
      sb.append("null");
    } else {
      sb.append(this.emitted);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("transferred:");
    if (this.transferred == null) {
      sb.append("null");
    } else {
      sb.append(this.transferred);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("specific:");
    if (this.specific == null) {
      sb.append("null");
    } else {
      sb.append(this.specific);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("rate:");
    sb.append(this.rate);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!is_set_emitted()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'emitted' is unset! Struct:" + toString());
    }

    if (!is_set_transferred()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'transferred' is unset! Struct:" + toString());
    }

    if (!is_set_specific()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'specific' is unset! Struct:" + toString());
    }

    if (!is_set_rate()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'rate' is unset! Struct:" + toString());
    }

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

  private static class ExecutorStatsStandardSchemeFactory implements SchemeFactory {
    public ExecutorStatsStandardScheme getScheme() {
      return new ExecutorStatsStandardScheme();
    }
  }

  private static class ExecutorStatsStandardScheme extends StandardScheme<ExecutorStats> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, ExecutorStats struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // EMITTED
            if (schemeField.type == org.apache.thrift.protocol.TType.MAP) {
              {
                org.apache.thrift.protocol.TMap _map268 = iprot.readMapBegin();
                struct.emitted = new HashMap<String,Map<String,Long>>(2*_map268.size);
                String _key269;
                Map<String,Long> _val270;
                for (int _i271 = 0; _i271 < _map268.size; ++_i271)
                {
                  _key269 = iprot.readString();
                  {
                    org.apache.thrift.protocol.TMap _map272 = iprot.readMapBegin();
                    _val270 = new HashMap<String,Long>(2*_map272.size);
                    String _key273;
                    long _val274;
                    for (int _i275 = 0; _i275 < _map272.size; ++_i275)
                    {
                      _key273 = iprot.readString();
                      _val274 = iprot.readI64();
                      _val270.put(_key273, _val274);
                    }
                    iprot.readMapEnd();
                  }
                  struct.emitted.put(_key269, _val270);
                }
                iprot.readMapEnd();
              }
              struct.set_emitted_isSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // TRANSFERRED
            if (schemeField.type == org.apache.thrift.protocol.TType.MAP) {
              {
                org.apache.thrift.protocol.TMap _map276 = iprot.readMapBegin();
                struct.transferred = new HashMap<String,Map<String,Long>>(2*_map276.size);
                String _key277;
                Map<String,Long> _val278;
                for (int _i279 = 0; _i279 < _map276.size; ++_i279)
                {
                  _key277 = iprot.readString();
                  {
                    org.apache.thrift.protocol.TMap _map280 = iprot.readMapBegin();
                    _val278 = new HashMap<String,Long>(2*_map280.size);
                    String _key281;
                    long _val282;
                    for (int _i283 = 0; _i283 < _map280.size; ++_i283)
                    {
                      _key281 = iprot.readString();
                      _val282 = iprot.readI64();
                      _val278.put(_key281, _val282);
                    }
                    iprot.readMapEnd();
                  }
                  struct.transferred.put(_key277, _val278);
                }
                iprot.readMapEnd();
              }
              struct.set_transferred_isSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // SPECIFIC
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.specific = new ExecutorSpecificStats();
              struct.specific.read(iprot);
              struct.set_specific_isSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // RATE
            if (schemeField.type == org.apache.thrift.protocol.TType.DOUBLE) {
              struct.rate = iprot.readDouble();
              struct.set_rate_isSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, ExecutorStats struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.emitted != null) {
        oprot.writeFieldBegin(EMITTED_FIELD_DESC);
        {
          oprot.writeMapBegin(new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.STRING, org.apache.thrift.protocol.TType.MAP, struct.emitted.size()));
          for (Map.Entry<String, Map<String,Long>> _iter284 : struct.emitted.entrySet())
          {
            oprot.writeString(_iter284.getKey());
            {
              oprot.writeMapBegin(new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.STRING, org.apache.thrift.protocol.TType.I64, _iter284.getValue().size()));
              for (Map.Entry<String, Long> _iter285 : _iter284.getValue().entrySet())
              {
                oprot.writeString(_iter285.getKey());
                oprot.writeI64(_iter285.getValue());
              }
              oprot.writeMapEnd();
            }
          }
          oprot.writeMapEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.transferred != null) {
        oprot.writeFieldBegin(TRANSFERRED_FIELD_DESC);
        {
          oprot.writeMapBegin(new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.STRING, org.apache.thrift.protocol.TType.MAP, struct.transferred.size()));
          for (Map.Entry<String, Map<String,Long>> _iter286 : struct.transferred.entrySet())
          {
            oprot.writeString(_iter286.getKey());
            {
              oprot.writeMapBegin(new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.STRING, org.apache.thrift.protocol.TType.I64, _iter286.getValue().size()));
              for (Map.Entry<String, Long> _iter287 : _iter286.getValue().entrySet())
              {
                oprot.writeString(_iter287.getKey());
                oprot.writeI64(_iter287.getValue());
              }
              oprot.writeMapEnd();
            }
          }
          oprot.writeMapEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.specific != null) {
        oprot.writeFieldBegin(SPECIFIC_FIELD_DESC);
        struct.specific.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(RATE_FIELD_DESC);
      oprot.writeDouble(struct.rate);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class ExecutorStatsTupleSchemeFactory implements SchemeFactory {
    public ExecutorStatsTupleScheme getScheme() {
      return new ExecutorStatsTupleScheme();
    }
  }

  private static class ExecutorStatsTupleScheme extends TupleScheme<ExecutorStats> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, ExecutorStats struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      {
        oprot.writeI32(struct.emitted.size());
        for (Map.Entry<String, Map<String,Long>> _iter288 : struct.emitted.entrySet())
        {
          oprot.writeString(_iter288.getKey());
          {
            oprot.writeI32(_iter288.getValue().size());
            for (Map.Entry<String, Long> _iter289 : _iter288.getValue().entrySet())
            {
              oprot.writeString(_iter289.getKey());
              oprot.writeI64(_iter289.getValue());
            }
          }
        }
      }
      {
        oprot.writeI32(struct.transferred.size());
        for (Map.Entry<String, Map<String,Long>> _iter290 : struct.transferred.entrySet())
        {
          oprot.writeString(_iter290.getKey());
          {
            oprot.writeI32(_iter290.getValue().size());
            for (Map.Entry<String, Long> _iter291 : _iter290.getValue().entrySet())
            {
              oprot.writeString(_iter291.getKey());
              oprot.writeI64(_iter291.getValue());
            }
          }
        }
      }
      struct.specific.write(oprot);
      oprot.writeDouble(struct.rate);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, ExecutorStats struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      {
        org.apache.thrift.protocol.TMap _map292 = new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.STRING, org.apache.thrift.protocol.TType.MAP, iprot.readI32());
        struct.emitted = new HashMap<String,Map<String,Long>>(2*_map292.size);
        String _key293;
        Map<String,Long> _val294;
        for (int _i295 = 0; _i295 < _map292.size; ++_i295)
        {
          _key293 = iprot.readString();
          {
            org.apache.thrift.protocol.TMap _map296 = new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.STRING, org.apache.thrift.protocol.TType.I64, iprot.readI32());
            _val294 = new HashMap<String,Long>(2*_map296.size);
            String _key297;
            long _val298;
            for (int _i299 = 0; _i299 < _map296.size; ++_i299)
            {
              _key297 = iprot.readString();
              _val298 = iprot.readI64();
              _val294.put(_key297, _val298);
            }
          }
          struct.emitted.put(_key293, _val294);
        }
      }
      struct.set_emitted_isSet(true);
      {
        org.apache.thrift.protocol.TMap _map300 = new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.STRING, org.apache.thrift.protocol.TType.MAP, iprot.readI32());
        struct.transferred = new HashMap<String,Map<String,Long>>(2*_map300.size);
        String _key301;
        Map<String,Long> _val302;
        for (int _i303 = 0; _i303 < _map300.size; ++_i303)
        {
          _key301 = iprot.readString();
          {
            org.apache.thrift.protocol.TMap _map304 = new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.STRING, org.apache.thrift.protocol.TType.I64, iprot.readI32());
            _val302 = new HashMap<String,Long>(2*_map304.size);
            String _key305;
            long _val306;
            for (int _i307 = 0; _i307 < _map304.size; ++_i307)
            {
              _key305 = iprot.readString();
              _val306 = iprot.readI64();
              _val302.put(_key305, _val306);
            }
          }
          struct.transferred.put(_key301, _val302);
        }
      }
      struct.set_transferred_isSet(true);
      struct.specific = new ExecutorSpecificStats();
      struct.specific.read(iprot);
      struct.set_specific_isSet(true);
      struct.rate = iprot.readDouble();
      struct.set_rate_isSet(true);
    }
  }

}

