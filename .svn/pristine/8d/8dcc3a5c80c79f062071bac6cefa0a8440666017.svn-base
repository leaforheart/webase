namespace java com.inveno.cps.dictionary.thrift

struct Dictionary {
  1: optional string id,
  2: optional string parentId,
  3: optional string childType,
  4: optional string code,
  5: optional string typeName,
  6: optional string properties,
  7: optional i32 status,
  8: optional string memo,
  9: optional i32 type
}

service DictionaryService {
	//查询字典列表，分页，根据父类类型过滤
	map<string,string> queDictionary(1:map<string,string> queryMap),
	//增加字典(有可能新增父类类型)
	map<string,string> addDictionary(1:Dictionary dictionary),
	//修改字典(有可能修改父类类型)
	map<string,string> updDictionary(1:Dictionary dictionary),
	//根据主键查看字典
	map<string,string> getDictionary(1:string id),
	//查询所有父类类型
	map<string,string> queParentType(),
	//根据父类类型id，获取该父类类型
	map<string,string> getParentType(1:string parentId),
	//判断连接是否断开
	void ping()
}