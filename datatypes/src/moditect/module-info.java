import com.fasterxml.jackson.databind.Module;

module com.fasterxml.jackson.datatype.jdk8 {
	exports com.fasterxml.jackson.datatype.jdk8;

	requires com.fasterxml.jackson.databind;

	provides Module with com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
}
