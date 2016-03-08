/**
 */

import javaposse.jobdsl.dsl.DslException

job('foo') {
  steps {
    shell('echo hi')
  }
}
