#ifndef JTSC_CORE_H
#define JTSC_CORE_H

/*struct vtable_java_lang_Object
{
	void(*equals)(Cjava_lang_Object*);
};

struct Cjava_lang_Object
{
	struct vtable_java_lang_Object* vtable;
	unsigned int __ref_count;
};

void Cjava_lang_Object_m_equals(CJava_lang_Object*);

struct vtable_java_lang_Object VTable_java_lang_Object = {
		.equals = &Cjava_lang_Object_m_equals;
};*/

namespace java
{
namespace lang
{

class Object
{

};

}
}

#endif
