import com.fasterxml.jackson.databind.Module;

module com.fasterxml.jackson.module.parameternames {
	exports com.fasterxml.jackson.module.paramnames;
	//optional
	requires static com.fasterxml.jackson.annotation;

	requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.core;

	provides Module with com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
}
