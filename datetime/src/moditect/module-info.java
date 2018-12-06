import com.fasterxml.jackson.databind.Module;

module com.fasterxml.jackson.datatype.jsr310 {
	exports com.fasterxml.jackson.datatype.jsr310;

	provides Module with com.fasterxml.jackson.datatype.jsr310.JSR310Module;

	requires transitive com.fasterxml.jackson.databind;
}
